package com.egorpoprotskiy.eyeofwages.month

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthCalculations
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.egorpoprotskiy.eyeofwages.network.CalendarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.String
import kotlin.text.toDoubleOrNull
import kotlin.text.toIntOrNull

class MonthEntryViewModel(private val monthRepository: MonthRepository): ViewModel() {
    var monthUiState by mutableStateOf(MonthUiState())
        private set
    private val _yearInput = MutableStateFlow("")
    private val _monthInput = MutableStateFlow("")
    //35
    init {
        viewModelScope.launch {
            // Пытаемся получить последнюю запись из БД
            val lastMonthEntry = monthRepository.getLastMonth().first() // Используем first() чтобы получить одно значение и завершить Flow
            lastMonthEntry?.let { month ->
                monthUiState = monthUiState.copy(
                    itemDetails = MonthDetails(
                        monthName = (month.monthName+1).toString(),
                        yearName = month.yearName.toString(),
                        oklad = month.oklad.toString(),
//                        norma = month.norma.toString(),
//                        rabTime = month.rabTime.toString(),
//                        nochTime = month.nochTime.toString(),
//                        prazdTime = month.prazdTime.toString(),
                        premia = month.premia.toString(),
                        visluga = month.visluga.toString(),
//                        prikazDen = month.prikazDen.toString(),
//                        prikazNoch = month.prikazNoch.toString(),
//                        itog = month.itog.toString()
                    )
                )
            }
            // Получаем текущий год и месяц
//            val currentYearMonth = YearMonth.now()
//            val currentYear = currentYearMonth.year.toString()
//            val currentMonth = currentYearMonth.monthValue.toString()

// Обновляем UI-состояние и StateFlow с текущими данными
            updateUiState(
                monthUiState.itemDetails.copy(
                    yearName = monthUiState.itemDetails.yearName,
                    monthName = monthUiState.itemDetails.monthName
                )
            )
        }

        viewModelScope.launch {
            combine(_yearInput, _monthInput) { year, month ->
                Pair(year, month)
            }.collect { (year, month) ->
                val yearInt = year.toIntOrNull()
                val monthInt = month.toIntOrNull()

                if (yearInt != null && monthInt != null && yearInt in 1900..2100 && monthInt in 1..12) {
                    fetchNorma(yearInt, monthInt)
                } else if (monthUiState.itemDetails.norma.isNotBlank()) {
                    updateUiState(monthUiState.itemDetails.copy(norma = ""))
                }
            }
        }
    }
    fun updateUiState(itemDetails: MonthDetails) {
        monthUiState =
            MonthUiState(
                itemDetails = itemDetails,
                isEntryValid = validateInput(itemDetails)
            )
        if (_yearInput.value != itemDetails.yearName) {
            _yearInput.value = itemDetails.yearName
        }
        if (_monthInput.value != itemDetails.monthName) {
            _monthInput.value = itemDetails.monthName
        }
    }

    private suspend fun fetchNorma(year: Int, month: Int) {
        try {
            val response = CalendarApi.retrofitService.getMonthCalendarData(year, month)
            if (response.isSuccessful) {
                val calendarResponse = response.body()
                calendarResponse?.let {
                    // Используем workHours
                    val parsedNorma = it.month.workingHours.toString() // Или it.calculationInfo?.norma?.replace(" часов", "")
                    if (!parsedNorma.isNullOrBlank()) {
                        updateUiState(monthUiState.itemDetails.copy(norma = parsedNorma))
                    } else {
                        updateUiState(monthUiState.itemDetails.copy(norma = ""))
                        println("Норма не найдена в ответе API.")
                    }
                }
            } else {
                updateUiState(monthUiState.itemDetails.copy(norma = ""))
                println("Error fetching norma: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            updateUiState(monthUiState.itemDetails.copy(norma = ""))
            println("Exception fetching norma: ${e.message}")
        }
    }

    private fun validateInput(uiState: MonthDetails = monthUiState.itemDetails): Boolean {
        return uiState.oklad.isNotBlank() && uiState.norma.isNotBlank()
    }

    suspend fun saveItem() {
        // Проверка валидации, если она есть
        if (!validateInput()) return
        // 1. Создаем Entity из UI-данных
        val currentMonthEntity = monthUiState.itemDetails.toItem()
        val daysToTake = currentMonthEntity.otpuskDays // Дни отпуска, введенные пользователем
        // 2. РАСЧЕТ ЗП (Brutto) для ТЕКУЩЕГО месяца
        // Вызов существующей функции MonthCalculations
        val calculationResults = MonthCalculations(currentMonthEntity)
        val calculatedBrutto = calculationResults.itogBezNdfl
        // ---------------------------------------------------------------------
        // НАЧАЛО РАСЧЕТА СРЕДНЕГО ДНЕВНОГО ЗАРАБОТКА (СДЗ)
        // ---------------------------------------------------------------------
        // 3. Получаем 12 месяцев ИЗ РЕПОЗИТОРИЯ
        // Используем .first() для получения одного значения Flow
        val lastMonths = monthRepository.getlist12Month().first()
        // 4. ФИЛЬТРАЦИЯ: Исключаем текущий месяц из списка для СДЗ
        val last12MonthsForSdz = lastMonths
            .filter { month ->
                // Убеждаемся, что текущий месяц не включен в расчетный период СДЗ
                !(month.yearName == currentMonthEntity.yearName && month.monthName == currentMonthEntity.monthName)
            }
            .take(12) // Берем только 12 месяцев
        // 5. РАССЧЕТ ЧИСЛИТЕЛЯ И ЗНАМЕНАТЕЛЯ СДЗ
        // ЧИСЛИТЕЛЬ: Общая чистая база начислений (Brutto - Исключения)
        val totalSdzBase = calculateTotalCleanAccrual(last12MonthsForSdz)
        // ЗНАМЕНАТЕЛЬ: Общее скорректированное количество дней
        val totalAdjustedDays = calculateTotalAdjustedDays(last12MonthsForSdz)

        // 6. РАССЧЕТ СДЗ И СУММЫ ОТПУСКНЫХ
        val sdz = if (totalAdjustedDays > 0) totalSdzBase / totalAdjustedDays else 0.0
        // Фиксация рассчитанной суммы отпускных
        val finalOtpuskPay = if (daysToTake > 0) {
            round2(sdz * daysToTake) // Умножаем СДЗ на введенные дни
        } else {
            // Если дни не введены, сохраняем 0 или введенную сумму
            currentMonthEntity.otpuskPay
        }
        // ---------------------------------------------------------------------
        // 7. СОХРАНЕНИЕ
        // ---------------------------------------------------------------------
        val monthToSave = currentMonthEntity.copy(
            // Сохраняем рассчитанное Brutto
            itogBezNdfl = calculatedBrutto,
            // Сохраняем рассчитанные Отпускные
            otpuskPay = finalOtpuskPay,
            // ... (другие рассчитанные поля, если вы их обновляете)
            itog = calculationResults.itog, // Например
        )
        Log.d("DEBUG_SAVE", "Final OtpuskPay to Save: $finalOtpuskPay")
        // Обновление/вставка в БД
        monthRepository.insertMonth(monthToSave)
    }
}
// ---------------------------------------------------------------------
// ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
// ---------------------------------------------------------------------
/**
 * Рассчитывает общую ЧИСТУЮ базу начислений за 12 месяцев (ЧИСЛИТЕЛЬ СДЗ).
 * Brutto - (Больничные + Отпускные).
 */
private fun calculateTotalCleanAccrual(last12Months: List<Month>): Double {
    if (last12Months.isEmpty()) return 0.0

    return last12Months.sumOf { month ->
        // Выплаты, не идущие в СДЗ:
        val nonSDZAccruals = month.bolnichniy + month.otpuskPay
        // Чистая база для СДЗ
        month.itogBezNdfl - nonSDZAccruals
    }
}
/**
 * Рассчитывает Общее скорректированное количество календарных дней (ЗНАМЕНАТЕЛЬ СДЗ).
 * 29.3 * (Отработанные часы / Норма часов).
 */
private fun calculateTotalAdjustedDays(last12Months: List<Month>): Double {
    val AVG_CALENDAR_DAYS = 29.3
    if (last12Months.isEmpty()) return 0.0

    return last12Months.sumOf { month ->
        if (month.norma == 0) 0.0 else {
            AVG_CALENDAR_DAYS * (month.rabTime.toDouble() / month.norma.toDouble())
        }
    }
}
/**
 * Функция округления до двух знаков (скорее всего, у вас уже есть).
 */
private fun round2(value: Double): Double =
    BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()

data class MonthUiState(
    val itemDetails: MonthDetails = MonthDetails(),
    val isEntryValid: Boolean = false
)

data class MonthDetails(
    val id: Int = 0,
    val monthName: String = "",
    val yearName: String = "",
    val oklad: String = "",
    val norma: String = "",
    val rabTime: String = "",
    val nochTime: String = "",
    val prazdTime: String = "",
    val premia: String = "40",
    var visluga: String = "0",
    val prikazDen: String = "",
    val prikazNoch: String = "",
    val itog: String = "",

    var itogBezNdfl: String = "", //сумма до вычета ндфл
    val bolnichniy: String = "", // Сумма, исключаемая из СДЗ (Больничные, пособия)
//    val inputOtpuskDay: String = ""
    val otpuskDays: String = "", // Дни отпуска (если ввод <= 39)
    val otpuskPay: String = "", // Сумма отпускных (введенная или рассчитанная)
    val otherPayments: String = ""

)

fun MonthDetails.toItem(): Month = Month (
    id = id,
    monthName = monthName.toIntOrNull() ?: 1,
    yearName = yearName.toIntOrNull() ?: 2025,
    oklad = oklad.toDoubleOrNull() ?: 0.0,
    norma = norma.toIntOrNull() ?: 0,
    rabTime = rabTime.toIntOrNull() ?: 0,
    nochTime = nochTime.toIntOrNull() ?: 0,
    prazdTime = prazdTime.toIntOrNull() ?: 0,
    premia = premia.toDoubleOrNull() ?: 0.0,
    visluga = visluga.toIntOrNull() ?: 0,
    prikazDen = prikazDen.toIntOrNull() ?: 0,
    prikazNoch = prikazNoch.toIntOrNull() ?: 0,
    itog = itog.toDoubleOrNull() ?: 0.0,
    itogBezNdfl = itogBezNdfl.toDoubleOrNull() ?: 0.0,
    bolnichniy = bolnichniy.toDoubleOrNull() ?: 0.0,
    otpuskDays = otpuskDays.toIntOrNull() ?: 0,
    otpuskPay = otpuskPay.toDoubleOrNull() ?: 0.0,
    otherPayments = otherPayments.toDoubleOrNull() ?: 0.0
)

fun Month.toItemUiState(isEntryValid: Boolean = false): MonthUiState = MonthUiState(
    itemDetails = this.toMonthDetails(),
    isEntryValid = isEntryValid
)

fun Month.toMonthDetails(): MonthDetails = MonthDetails(
    id = id,
    monthName = monthName.toString(),
    yearName = yearName.toString(),
    oklad = oklad.toString(),
    norma = norma.toString(),
    rabTime = rabTime.toString(),
    nochTime = nochTime.toString(),
    prazdTime = prazdTime.toString(),
    premia = premia.toString(),
    visluga = visluga.toString(),
    prikazDen = prikazDen.toString(),
    prikazNoch = prikazNoch.toString(),
    itog = itog.toString(),
    itogBezNdfl = itogBezNdfl.toString(),
    bolnichniy = bolnichniy.toString(),
    otpuskDays = otpuskDays.toString(),
    otpuskPay = otpuskPay.toString(),
)