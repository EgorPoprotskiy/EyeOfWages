package com.egorpoprotskiy.eyeofwages.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "month") //это будет data class в таблице БД(month)
data class Month(
    @PrimaryKey(autoGenerate = true) //автоинкрементный первичный ключ(autoGenerate = true — значение будет автоматически генерироваться Room)
    val id: Int = 0,
    @ColumnInfo(name = "monthName")
    val monthName: Int,
    @ColumnInfo(name = "yearName")
    val yearName: Int,
    @ColumnInfo(name = "oklad") //Столбцы таблицы в БД.
    val oklad: Double,
    @ColumnInfo(name = "norma")
    val norma: Int,
    @ColumnInfo(name = "rabTime")
    val rabTime: Int,
    @ColumnInfo(name = "nochTime")
    val nochTime: Int,
    @ColumnInfo(name = "prazdTime")
    val prazdTime: Int,
    @ColumnInfo(name = "premia")
    val premia: Double,
    @ColumnInfo(name = "visluga")
    val visluga: Int,
    @ColumnInfo(name = "prikazDen")
    val prikazDen: Int,
    @ColumnInfo(name = "prikaz")
    val prikazNoch: Int,
    @ColumnInfo(name = "itog")
    var itog: Double,
    @ColumnInfo(name = "itogBezNdfl")
    var itogBezNdfl: Double, //сумма до вычета ндфл
    @ColumnInfo(name = "bolnichniy")
    val bolnichniy: Double, // Сумма, исключаемая из СДЗ (Больничные, пособия)
    @ColumnInfo(name = "votpuskDays")
    val otpuskDays: Int, // Дни отпуска (если ввод <= 39)
    @ColumnInfo(name = "otpuskPay")
    val otpuskPay: Double // Сумма отпускных (введенная или рассчитанная)
)