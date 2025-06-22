package com.egorpoprotskiy.eyeofwages.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthDao {
    // Определите методы для операций базы данных здесь
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(month: Month)

    @Update
    suspend fun update(month: Month)

    @Delete
    suspend fun delete(month: Month)

    @Query("SELECT * FROM month WHERE id = :id")
    fun getMonth(id: Int): Flow<Month?>

    @Query("SELECT * FROM Month ORDER BY id ASC")
    fun getAllMonths(): Flow<List<Month>>
}