package com.egorpoprotskiy.eyeofwages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(val monthRepository: MonthRepository) : ViewModel() {
    //Состояние для PULL-TO-REFRESH
    // Скрытый MutableStateFlow для изменения состояния
    private val _isRefreshing = MutableStateFlow(false)
    // Открытый StateFlow, который Composable будет собирать
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    //val homeUiState: StateFlow<HomeUiSate> — это поток данных, который содержит состояние UI для экрана домашнего списка месяцев.
    //Через monthRepository.getAllMonthStream() ты получаешь Flow<List<Month>> из базы данных.
    val homeUiState: StateFlow<HomeUiSate> = monthRepository.getAllMonthStream()
        .map { HomeUiSate(it) } //преобразует список месяцев в UI-состояние.
        .stateIn( //онвертирует поток в StateFlow, который можно наблюдать из UI, и поддерживает кэширование и подписки.
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiSate()
        )
    //Запускает корутину в viewModelScope и вызывает удаление месяца из репозитория (а значит — из базы).
    fun deleteMonth(month: Month) {
        viewModelScope.launch {
            monthRepository.deleteMonth(month)
        }
    }
    //Функция обновления данных для PULL-TO-REFRESH
    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                delay(1000)
            } catch (e: Exception) {

            }finally {
                _isRefreshing.value = false
            }
        }
    }

//задаёт время, в течение которого подписка остаётся активной, даже если UI временно не наблюдает (например, при смене конфигурации).
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiSate(val monthList: List<Month> = listOf()) //Просто оболочка для списка месяцев, которую удобно передавать в UI.