package com.egorpoprotskiy.eyeofwages.data

import kotlinx.coroutines.flow.Flow
//отделили логику хранения от остального кода (ViewModel, UI),
class OfflineMonthRepository(private val monthDao: MonthDao) : MonthRepository {
    //Просто передаёт вызов DAO.
    //Возвращает поток всех записей Month.
    override fun getAllMonthStream(): Flow<List<Month>> = monthDao.getAllMonths()
    //То же самое, но по id.
    override fun getMonthStream(id: Int): Flow<Month?> = monthDao.getMonth(id)
    //Асинхронные методы — просто проксируют вызовы к DAO.
    override suspend fun insertMonth(month: Month) { monthDao.insert(month) }
    //Асинхронные методы — просто проксируют вызовы к DAO.
    override suspend fun deleteMonth(month: Month) { monthDao.delete(month) }
    //Асинхронные методы — просто проксируют вызовы к DAO.
    override suspend fun updateMonth(month: Month) { monthDao.update(month) }
}