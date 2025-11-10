package com.egorpoprotskiy.eyeofwages.month

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthCalculateData
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import com.egorpoprotskiy.eyeofwages.data.MonthCalculations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MonthDetailsViewModel (
    savedStateHandle: SavedStateHandle,
    private val monthRepository: MonthRepository
) : ViewModel() {
    private val monthId: Int = checkNotNull(savedStateHandle[MonthDetailsDestination.monthIdArgs])

    //20
    private val _calculateData = MutableStateFlow(MonthCalculateData())
    val calculateData: StateFlow<MonthCalculateData> = _calculateData
    private val _uiState = MutableStateFlow(MonthDetailsUiState())
    val uiState: StateFlow<MonthDetailsUiState> = _uiState

    private val _totalSdzBase = MutableStateFlow(0.0)
    val totalSdzBase: StateFlow<Double> = _totalSdzBase.asStateFlow()

    suspend fun deleteMonth() {
        monthRepository.deleteMonth(uiState.value.monthDetails.toItem())
    }

    init {
        viewModelScope.launch {
            monthRepository.getMonthStream(monthId).filterNotNull().collect { month ->
                Log.d("DEBUG_DETAIL_READ", "Значение otpuskPay, прочитанное из БД: ${month.otpuskPay}")
                _uiState.value = MonthDetailsUiState(monthDetails = month.toMonthDetails())
                _calculateData.value = MonthCalculations(month)
            }
        }
        //
        viewModelScope.launch {
            monthRepository.getlist12Month().collect { list ->
                Log.d("DEBUG_SDZ", "Получено месяцев: ${list.size}")
                list.forEach { month ->
                    // Проверим значения Brutto в каждом месяце
                    Log.d("DEBUG_SDZ", "Месяц ${month.monthName}: Brutto (itogBezNdfl) = ${month.itogBezNdfl}")
                }
                _totalSdzBase.value = calculateSimpleTotal(list)
                Log.d("DEBUG_SDZ", "Финальная сумма: ${calculateSimpleTotal(list)}") // Проверим финальный результат
            }
        }
    }

    private fun calculateSimpleTotal(last12Months: List<Month>): Double {
        if (last12Months.isEmpty()) {
            return  0.0
        }
        return last12Months.sumOf { month ->
            month.itogBezNdfl
        }
    }
}

data class MonthDetailsUiState(
    val outOfStock: Boolean = true,
    val monthDetails: MonthDetails = MonthDetails()
)