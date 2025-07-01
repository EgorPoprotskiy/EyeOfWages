package com.egorpoprotskiy.eyeofwages.month

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.MonthCalculateData
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import com.egorpoprotskiy.eyeofwages.data.calculateMonthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

//    val uiState: StateFlow<MonthDetailsUiState> =
//        monthRepository.getMonthStream(monthId)
//            .filterNotNull()
//            .map {
//                MonthDetailsUiState(outOfStock = it.oklad <= 0, monthDetails = it.toMonthDetails())
//            }.stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = MonthDetailsUiState()
//            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun deleteMonth() {
        monthRepository.deleteMonth(uiState.value.monthDetails.toItem())
    }

    init {
        viewModelScope.launch {
            monthRepository.getMonthStream(monthId).filterNotNull().collect { month ->
                _uiState.value = MonthDetailsUiState(monthDetails = month.toMonthDetails())
                _calculateData.value = calculateMonthData(month)
            }
        }
    }

}


data class MonthDetailsUiState(
    val outOfStock: Boolean = true,
    val monthDetails: MonthDetails = MonthDetails()
)