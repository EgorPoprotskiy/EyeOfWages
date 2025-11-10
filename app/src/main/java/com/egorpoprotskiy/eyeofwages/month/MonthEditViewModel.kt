package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthCalculations
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import com.egorpoprotskiy.eyeofwages.network.CalendarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class MonthEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val monthRepository: MonthRepository
) : ViewModel() {
    var monthUiState by mutableStateOf(MonthUiState())
        private set
    private val monthId: Int = checkNotNull(savedStateHandle[MonthEditDestination.monthIdArg])
    //41 для автоматического обновления нормы часов с сайта (REST API)
    private val _yearInput = MutableStateFlow("")
    private val _monthInput = MutableStateFlow("")

    private fun validateInput(uiState: MonthDetails = monthUiState.itemDetails): Boolean {
        return uiState.oklad.isNotBlank() && uiState.norma.isNotBlank()
    }

    init {
        viewModelScope.launch {
            monthUiState = monthRepository.getMonthStream(monthId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
            //41
            _yearInput.value = monthUiState.itemDetails.yearName
            _monthInput.value = monthUiState.itemDetails.monthName
        }

        // Запускаем сборщик для отслеживания изменений _yearInput и _monthInput
        viewModelScope.launch {
            combine(_yearInput, _monthInput) { year, month ->
                Pair(year, month)
            }.collect { (year, month) ->
                val yearInt = year.toIntOrNull()
                val monthInt = month.toIntOrNull()

                // Дополнительная проверка: если норму уже получили из БД,
                // не перезапрашиваем её сразу же, чтобы избежать лишнего запроса
                // (хотя API может быть и idempotent).
                // Но если год/месяц изменились, то запрос нужен.
                if (yearInt != null && monthInt != null && yearInt in 1900..2100 && monthInt in 1..12) {
                    // Проверяем, отличается ли текущая norma от той, что в UI,
                    // и соответствует ли она году/месяцу, чтобы избежать бесконечного цикла,
                    // если API возвращает то же значение
                    fetchNorma(yearInt, monthInt)
                } else if (monthUiState.itemDetails.norma.isNotBlank()) {
                    // Очищаем норму, если ввод некорректен
                    updateUiState(monthUiState.itemDetails.copy(norma = ""))
                }
            }
        }
    }

    fun updateUiState(itemDetails: MonthDetails) {
        monthUiState = MonthUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
        //41 Обновляем _yearInput и _monthInput для запуска запроса нормы
        if (_yearInput.value != itemDetails.yearName) {
            _yearInput.value = itemDetails.yearName
        }
        if (_monthInput.value != itemDetails.monthName) {
            _monthInput.value = itemDetails.monthName
        }
    }

    // Эта функция идентична той, что в MonthEntryViewModel
    private suspend fun fetchNorma(year: Int, month: Int) {
        try {
            val response = CalendarApi.retrofitService.getMonthCalendarData(year, month)
            if (response.isSuccessful) {
                val calendarResponse = response.body()
                calendarResponse?.let {
                    val parsedNorma = it.month.workingHours.toString()
                    if (!parsedNorma.isNullOrBlank()) {
                        // Важно: проверяем, что полученная норма отличается, чтобы не вызывать лишнее обновление UI
                        if (monthUiState.itemDetails.norma != parsedNorma) {
                            updateUiState(monthUiState.itemDetails.copy(norma = parsedNorma))
                        }
                    } else {
                        if (monthUiState.itemDetails.norma.isNotBlank()) { // Очищаем, только если не пусто
                            updateUiState(monthUiState.itemDetails.copy(norma = ""))
                        }
                        println("Норма (workingHours) не найдена в ответе API.")
                    }
                }
            } else {
                if (monthUiState.itemDetails.norma.isNotBlank()) { // Очищаем, только если не пусто
                    updateUiState(monthUiState.itemDetails.copy(norma = ""))
                }
                println("Error fetching norma: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            if (monthUiState.itemDetails.norma.isNotBlank()) { // Очищаем, только если не пусто
                updateUiState(monthUiState.itemDetails.copy(norma = ""))
            }
            println("Exception fetching norma: ${e.message}")
        }
    }

    suspend fun updateItem() {
        // Код до расчета отпусных.
//        if (validateInput(monthUiState.itemDetails)) {
//            monthRepository.updateMonth(monthUiState.itemDetails.toItem())
//        }
        if (!validateInput()) return
        // 1. Создаем Entity из UI-данных
        val currentMonthEntity = monthUiState.itemDetails.toItem()
        val daysToTake = currentMonthEntity.otpuskDays // Дни отпуска
        // 2. РАСЧЕТ ЗП (Brutto) для ТЕКУЩЕГО месяца
        // Используем ВАШУ существующую функцию MonthCalculations
        val calculationResults = MonthCalculations(currentMonthEntity)
        val calculatedBrutto = calculationResults.itogBezNdfl
        // ---------------------------------------------------------------------
        // НАЧАЛО РАСЧЕТА СДЗ (Для расчета отпускных)
        // ---------------------------------------------------------------------
        // 3. Получаем 12 месяцев ИЗ РЕПОЗИТОРИЯ
        val lastMonths = monthRepository.getlist12Month().first()
        // 4. ФИЛЬТРАЦИЯ: Исключаем ТЕКУЩУЮ запись из списка для СДЗ
        // (Поскольку это редактирование, мы исключаем ее из расчетного периода)
        val last12MonthsForSdz = lastMonths
            .filter { month ->
                !(month.yearName == currentMonthEntity.yearName && month.monthName == currentMonthEntity.monthName)
            }
            .take(12)
        // 5. РАССЧИТЫВАЕМ ЧИСЛИТЕЛЬ И ЗНАМЕНАТЕЛЬ СДЗ (Используем вспомогательные функции)
        val totalSdzBase = calculateTotalCleanAccrual(last12MonthsForSdz)
        val totalAdjustedDays = calculateTotalAdjustedDays(last12MonthsForSdz)
        // 6. РАССЧИТЫВАЕМ СДЗ И СУММУ ОТПУСКНЫХ
        val sdz = if (totalAdjustedDays > 0) totalSdzBase / totalAdjustedDays else 0.0
        val finalOtpuskPay = if (daysToTake > 0) {
            round2(sdz * daysToTake)
        } else {
            currentMonthEntity.otpuskPay
        }
        // ---------------------------------------------------------------------
        // 7. СОХРАНЕНИЕ (ОБНОВЛЕНИЕ)
        // ---------------------------------------------------------------------
        val monthToSave = currentMonthEntity.copy(
            // ОБЯЗАТЕЛЬНО: Фиксируем рассчитанное Brutto
            itogBezNdfl = calculatedBrutto,
            // ОБЯЗАТЕЛЬНО: Фиксируем рассчитанные Отпускные
            otpuskPay = finalOtpuskPay,

            // ... (другие рассчитанные поля, такие как itog, ndfl и т.д.)
            itog = calculationResults.itog
        )
        // ОБНОВЛЕНИЕ В БД
        monthRepository.updateMonth(monthToSave) // Предполагаем, что у вас есть updateMonth
    }
}
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
