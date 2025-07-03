package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
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

    private fun validateInput(uiState: MonthDetails = monthUiState.itemDetails): Boolean {
        return uiState.oklad.isNotBlank() && uiState.norma.isNotBlank()
    }

    init {
        viewModelScope.launch {
            monthUiState = monthRepository.getMonthStream(monthId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
        }
    }

    fun updateUiState(itemDetails: MonthDetails) {
        monthUiState = MonthUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
    }

    suspend fun updateItem() {
        if (validateInput(monthUiState.itemDetails)) {
            monthRepository.updateMonth(monthUiState.itemDetails.toItem())
        }
    }
}