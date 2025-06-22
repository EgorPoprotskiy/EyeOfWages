package com.egorpoprotskiy.eyeofwages.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Month::class], version = 1, exportSchema = false)
abstract class MonthDatabase: RoomDatabase() {
    abstract fun monthDao(): MonthDao

    companion object {
        @Volatile
        private var instance: MonthDatabase? = null

        fun getDatabase(context: Context): MonthDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MonthDatabase::class.java,
                    "month_database"
                )
                    .build()
                    .also { instance = it }
            }
        }
    }
}