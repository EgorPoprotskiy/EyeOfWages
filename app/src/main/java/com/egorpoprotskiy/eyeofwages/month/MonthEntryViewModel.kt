package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.network.CalendarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

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
        if (validateInput()) {
            monthRepository.insertMonth(monthUiState.itemDetails.toItem())
        }
    }
}

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
    val itog: String = ""
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
    itog = itog.toDoubleOrNull() ?: 0.0
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
    itog = itog.toString()
)