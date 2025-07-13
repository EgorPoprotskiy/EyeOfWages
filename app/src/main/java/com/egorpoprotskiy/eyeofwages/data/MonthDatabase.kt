package com.egorpoprotskiy.eyeofwages.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//entities = [Month::class] — перечислены все таблицы (в данном случае одна: Month).
//version = 1 — версия схемы базы данных (нужно увеличивать при изменении структуры таблиц).
//exportSchema = false — не сохранять схему базы данных в файл (можно поставить true для отладки и тестов).
@Database(entities = [Month::class], version = 5, exportSchema = false)
abstract class MonthDatabase: RoomDatabase() {
    abstract fun monthDao(): MonthDao //метод, который сообщает Room, какой DAO интерфейс использовать.

    //создаёт синглтон базы данных, чтобы использовать один экземпляр в приложении.
    companion object {
        @Volatile //гарантирует, что instance будет всегда читаться из основной памяти, а не из кеша потока. Нужно для многопоточности.
        private var instance: MonthDatabase? = null

        //метод, который возвращает готовую базу данных:
        fun getDatabase(context: Context): MonthDatabase {
            return instance ?: synchronized(this) {
                //создаёт экземпляр базы данных.
                Room.databaseBuilder(
                    context,
                    MonthDatabase::class.java,
                    "month_database"
                )
//                    .fallbackToDestructiveMigration() // Удаляет данные в приложении при изменении схемы БД.
                    .build()
                    //сохраняет созданный экземпляр в переменную instance.
                    .also { instance = it }
            }
        }
    }
}