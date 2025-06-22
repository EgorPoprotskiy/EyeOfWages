package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.egorpoprotskiy.eyeofwages.data.Month

class MonthEntryViewModel: ViewModel() {
    var inputData by mutableStateOf<Month?>(null)
        private set
    fun setData(data: Month) {
        inputData = data
    }

    var oklad by mutableStateOf("0")
    var norma by mutableStateOf("0")
    var rabTime by mutableStateOf("0")
    var nochTime by mutableStateOf("0")
    var prazdTime by mutableStateOf("0")
    var premia by mutableStateOf("40")
    var visluga by mutableStateOf("0")
    var prikazDen by mutableStateOf("0")
    var prikaz by mutableStateOf("0")

    fun toMonth(): Month {
        return Month(
            oklad = oklad.toDoubleOrNull() ?: 0.0,
            norma = norma.toIntOrNull() ?: 0,
            rabTime = rabTime.toIntOrNull() ?: 0,
            nochTime = nochTime.toIntOrNull() ?: 0,
            prazdTime = prazdTime.toIntOrNull() ?: 0,
            premia = premia.toDoubleOrNull() ?: 0.0,
            visluga = visluga.toIntOrNull() ?: 0,
//            prikazDen = prikazDen.toIntOrNull() ?: 0,
            prikaz = prikaz.toIntOrNull() ?: 0
        )
    }
}