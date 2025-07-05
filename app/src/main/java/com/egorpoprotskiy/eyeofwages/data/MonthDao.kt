package com.egorpoprotskiy.eyeofwages.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/*MonthDao — это интерфейс доступа к базе данных (DAO), который описывает,
какие операции можно выполнять с таблицей month в Room (локальной БД в Android).
 */
@Dao //помечает интерфейс как DAO для Room, чтобы он мог сгенерировать реализацию в рантайме.
interface MonthDao {
    // Определите методы для операций базы данных здесь
    @Insert(onConflict = OnConflictStrategy.IGNORE) //если такой ID уже существует, новая запись не вставляется (конфликт игнорируется).
    suspend fun insert(month: Month)

    @Update
    suspend fun update(month: Month)

    @Delete
    suspend fun delete(month: Month)

    @Query("SELECT * FROM month WHERE id = :id")
    //Получает одну запись с конкретным id.
    fun getMonth(id: Int): Flow<Month?> //Flow<Month?> - можно слушать изменения в реальном времени

    //сортировка по id.
//    @Query("SELECT * FROM Month ORDER BY id ASC")
    //сортировка по году и месяцу.
    @Query("SELECT * FROM Month ORDER BY yearName DESC, monthName DESC")
    //Получает все записи из таблицы Month, отсортированные по id.
    fun getAllMonths(): Flow<List<Month>>
}