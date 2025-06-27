package com.egorpoprotskiy.eyeofwages.month

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MonthDetailsViewModel (
    savedStateHandle: SavedStateHandle,
    private val monthRepository: MonthRepository
) : ViewModel() {
    private val monthId: Int = checkNotNull(savedStateHandle[MonthDetailsDestination.monthIdArgs])

    val uiState: StateFlow<MonthDetailsUiState> =
        monthRepository.getMonthStream(monthId)
            .filterNotNull()
            .map {
                MonthDetailsUiState(outOfStock = it.oklad <= 0, monthDetails = it.toMonthDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MonthDetailsUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun deleteMonth() {
        monthRepository.deleteMonth(uiState.value.monthDetails.toItem())
    }
}

data class MonthDetailsUiState(
    val outOfStock: Boolean = true,
    val monthDetails: MonthDetails = MonthDetails()
)