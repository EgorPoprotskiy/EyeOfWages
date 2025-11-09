package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.MonthCalculations
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import com.egorpoprotskiy.eyeofwages.network.CalendarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        //41
        // Обновляем _yearInput и _monthInput для запуска запроса нормы
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
//        if (validateInput(monthUiState.itemDetails)) {
//            monthRepository.updateMonth(monthUiState.itemDetails.toItem())
//        }
        if (!validateInput()) return

        // 1. Получаем Entity из UI-данных
        // Важно: monthUiState.itemDetails уже должно содержать id существующей записи
        val existingMonthEntity = monthUiState.itemDetails.toItem()

        // 2. ВЫЗОВ РАСЧЕТА
        // Используем существующую Entity для расчета Brutto и прочих полей
        val calculationResults = MonthCalculations(existingMonthEntity)

        // 3. Создаем Entity для сохранения, ПЕРЕЗАПИСЫВАЯ РАССЧИТАННЫЕ значения
        val monthToSave = existingMonthEntity.copy(
            // ФИКСАЦИЯ РАССЧИТАННОГО BRUTTO:
            itogBezNdfl = calculationResults.itogBezNdfl,

            // Если вы также сохраняете другие рассчитанные поля (premia, itog),
            // обязательно обновите их здесь:
            itog = calculationResults.itog,
            // premia = calculationResults.premiaRub,

            // ...
        )

        // 4. Сохранение (обновление) в БД
        monthRepository.updateMonth(monthToSave) // Предполагаем, что у вас есть updateMonth
    }
}