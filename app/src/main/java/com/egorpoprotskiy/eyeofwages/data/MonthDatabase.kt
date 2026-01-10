package com.egorpoprotskiy.eyeofwages.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//entities = [Month::class] — перечислены все таблицы (в данном случае одна: Month).
//version = 1 — версия схемы базы данных (нужно увеличивать при изменении структуры таблиц).
//exportSchema = false — не сохранять схему базы данных в файл (можно поставить true для отладки и тестов).
@Database(
    entities = [Month::class],
    version = 7,
//    autoMigrations = [AutoMigration(from = 6, 7)],
    exportSchema = false)
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
                    /* Удаляет данные в приложении при изменении схемы БД.
                    Нужна только на этапе разработки.
   *********************В проде эту опцию надо ОБЯЗАТЕЛЬНО ОТКЛЮЧАТЬ!!!
                     */
//                    .fallbackToDestructiveMigration()
                    //Миграция БД с версии 6 на версию 7.
                    .addMigrations(MIGRATION_6_7)
                    .build()
                    //сохраняет созданный экземпляр в переменную instance.
                    .also { instance = it }
            }
        }
    }
}

// Создай этот объект где-нибудь, где он будет доступен, например, в том же файле MonthDatabase.kt
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQL-запрос для добавления нового столбца
        // ALTER TABLE month ADD COLUMN "notes" "TEXT" NOT NULL DEFAULT '';
        //notes  - новое поле в БД.
        // TEXT - тип данных для String в SQLite
        // NOT NULL - потому что в Kotlin поле String не может быть null
        // DEFAULT '' - значение по умолчанию для существующих записей
        database.execSQL("ALTER TABLE month ADD COLUMN otherPayments REAL NOT NULL DEFAULT 0.0")
    }
}