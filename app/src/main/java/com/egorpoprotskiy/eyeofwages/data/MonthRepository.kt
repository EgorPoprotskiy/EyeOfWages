package com.egorpoprotskiy.eyeofwages.data

import kotlinx.coroutines.flow.Flow
/*Интерфейс нужен для того, чтобы ты мог:
    -изолировать работу с БД от остальной логики (например, от ViewModel),
    -легко писать моки для тестирования,
    -использовать разные реализации репозитория (например, одна с Room, другая — с сетью или кешем).
 */

/*Этот интерфейс MonthRepository определяет контракт (или «договор») для репозитория,
который работает с сущностью Month. Вот кратко по каждой строчке.
*/
interface MonthRepository {
    //Возвращает поток (Flow) всех записей из таблицы month.
    //Используется для отображения всех месяцев, например, в списке.
    //Поток позволяет автоматически обновлять UI при изменениях в БД.
    fun getAllMonthStream(): Flow<List<Month>>
//    Возвращает конкретный Month по id как поток.
//    Удобно, если хочешь следить за изменением одной записи.
    fun getMonthStream(id: Int): Flow<Month?>
    //Добавляет новую запись в базу.
    //suspend — означает, что функция вызывается в корутине (асинхронно).
    suspend fun insertMonth(month: Month)
    //Удаляет запись из базы.
    suspend fun deleteMonth(month: Month)
    //Обновляет уже существующую запись.
    suspend fun updateMonth(month: Month)
    suspend fun getLastMonth(): Flow<Month?>
//    Возвращает список из последних 12 месяцев.
    suspend fun getlist12Month(): Flow<List<Month>>
}