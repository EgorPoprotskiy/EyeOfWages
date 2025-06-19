package com.egorpoprotskiy.eyeofwages.data

data class Month (
    val oklad: Int,
    val norma: Int,
    val rabTime: Int,
    val nochTime: Int,
    val prazdTime: Int,
    val premia: Int,
    val visluga: Int,
    val prikazDen: Int,
    val prikazNoch: Int
)