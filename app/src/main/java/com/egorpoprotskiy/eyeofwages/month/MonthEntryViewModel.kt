package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthRepository

class MonthEntryViewModel(private val monthRepository: MonthRepository): ViewModel() {
    var monthUiState by mutableStateOf(MonthUiState())
        private set

    fun updateUiState(itemDetails: MonthDetails) {
        monthUiState =
            MonthUiState(
                itemDetails = itemDetails,
                isEntryValid = validateInput(itemDetails)
            )
    }

    private fun validateInput(uiState: MonthDetails = monthUiState.itemDetails): Boolean {
        return uiState.oklad.isNotBlank() && uiState.norma.isNotBlank()
    }

    suspend fun saveItem() {
        if (validateInput()) {
            monthRepository.insertMonth(monthUiState.itemDetails.toItem())
        }
    }
//    var inputData by mutableStateOf<Month?>(null)
//        private set
//    fun setData(data: Month) {
//        inputData = data
//    }
//
//    var oklad by mutableStateOf("0")
//    var norma by mutableStateOf("0")
//    var rabTime by mutableStateOf("0")
//    var nochTime by mutableStateOf("0")
//    var prazdTime by mutableStateOf("0")
//    var premia by mutableStateOf("40")
//    var visluga by mutableStateOf("0")
//    var prikazDen by mutableStateOf("0")
//    var prikaz by mutableStateOf("0")
//    var itog by mutableStateOf("0")
//
//    fun toMonth(): Month {
//        return Month(
//            oklad = oklad.toDoubleOrNull() ?: 0.0,
//            norma = norma.toIntOrNull() ?: 0,
//            rabTime = rabTime.toIntOrNull() ?: 0,
//            nochTime = nochTime.toIntOrNull() ?: 0,
//            prazdTime = prazdTime.toIntOrNull() ?: 0,
//            premia = premia.toDoubleOrNull() ?: 0.0,
//            visluga = visluga.toIntOrNull() ?: 0,
////            prikazDen = prikazDen.toIntOrNull() ?: 0,
//            prikaz = prikaz.toIntOrNull() ?: 0,
//            itog = itog.toDoubleOrNull() ?: 0.0
//        )
//    }
}

data class MonthUiState(
    val itemDetails: MonthDetails = MonthDetails(),
    val isEntryValid: Boolean = false
)

data class MonthDetails(
    val id: Int = 0,
    val monthName: String = "Введите месяц",
    val yearName: String = "Введите год",
    val oklad: String = "0",
    val norma: String = "0",
    val rabTime: String = "0",
    val nochTime: String = "0",
    val prazdTime: String = "0",
    val premia: String = "40",
    var visluga: String = "0",
    val prikazDen: String = "0",
    val prikaz: String = "0",
    val itog: String = "0"
)

fun MonthDetails.toItem(): Month = Month (
    id = id,
    monthName = monthName.orEmpty() ?: "Апрель",
    yearName = yearName.orEmpty() ?: "2025",
    oklad = oklad.toDoubleOrNull() ?: 0.0,
    norma = norma.toIntOrNull() ?: 0,
    rabTime = rabTime.toIntOrNull() ?: 0,
    nochTime = nochTime.toIntOrNull() ?: 0,
    prazdTime = prazdTime.toIntOrNull() ?: 0,
    premia = premia.toDoubleOrNull() ?: 0.0,
    visluga = visluga.toIntOrNull() ?: 0,
//    prikazDen = prikazDen.toIntOrNull() ?: 0,
    prikaz = prikaz.toIntOrNull() ?: 0,
    itog = itog.toDoubleOrNull() ?: 0.0
)

fun Month.toItemUiState(isEntryValid: Boolean = false): MonthUiState = MonthUiState(
    itemDetails = this.toMonthDetails(),
    isEntryValid = isEntryValid
)

fun Month.toMonthDetails(): MonthDetails = MonthDetails(
//    id = id,
//    oklad = oklad.toString(),
//    norma = norma.toString(),
//    rabTime = rabTime.toString(),
//    nochTime = nochTime.toString(),
//    prazdTime = prazdTime.toString(),
//    premia = premia.toString(),
//    visluga = visluga.toString(),
////    prikazDen = prikazDen.toString(),
//    prikaz = prikaz.toString(),
//    itog = itog.toString()
    id = id,
    oklad = oklad.toString(),
    norma = norma.toString(),
    rabTime = rabTime.toString(),
    nochTime = nochTime.toString(),
    prazdTime = prazdTime.toString(),
    premia = premia.toString(),
    visluga = visluga.toString(),
//    prikazDen = prikazDen.toString(),
    prikaz = prikaz.toString(),
    itog = itog.toString()
)