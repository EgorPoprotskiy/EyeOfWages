package com.egorpoprotskiy.eyeofwages.data

import kotlinx.coroutines.flow.Flow

interface MonthRepository {
    fun getAllMonthsStream(): Flow<List<Month>>
    fun getMonthStream(id: Int): Flow<Month?>
    suspend fun insertMonth(month: Month)
    suspend fun deleteMonth(month: Month)
    suspend fun updateMonth(month: Month)
}