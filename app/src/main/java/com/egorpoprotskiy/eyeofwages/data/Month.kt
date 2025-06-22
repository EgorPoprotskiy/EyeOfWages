package com.egorpoprotskiy.eyeofwages.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "month")
data class Month (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "oklad")
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
//    @ColumnInfo(name = "prikazDen")
//    val prikazDen: Int,
    @ColumnInfo(name = "prikaz")
    val prikaz: Int
)