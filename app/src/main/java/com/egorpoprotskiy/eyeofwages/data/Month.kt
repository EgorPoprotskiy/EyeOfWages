package com.egorpoprotskiy.eyeofwages.data

data class Month (
    val oklad: Double,
    val norma: Int,
    val rabTime: Int,
    val nochTime: Int,
    val prazdTime: Int,
    val premia: Double,
    val visluga: Int,
    val prikazDen: Int,
    val prikazNoch: Int
)