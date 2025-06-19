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
}