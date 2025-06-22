package com.egorpoprotskiy.eyeofwages.data

import kotlinx.coroutines.flow.Flow

class OfflineMonthRepository(private val monthDao: MonthDao) : MonthRepository {

    override fun getAllMonthsStream(): Flow<List<Month>> = monthDao.getAllMonths()

    override fun getMonthStream(id: Int): Flow<Month?> = monthDao.getMonth(id)

    override suspend fun insertMonth(month: Month) { monthDao.insert(month) }

    override suspend fun deleteMonth(month: Month) { monthDao.delete(month) }

    override suspend fun updateMonth(month: Month) { monthDao.update(month) }
}