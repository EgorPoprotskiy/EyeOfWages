package com.egorpoprotskiy.eyeofwages.network

import com.google.gson.annotations.SerializedName

data class CalendarResponse(
    val year: Int,
    val month: MonthData,
    val status: Int
)
data class MonthData (
    val id: Int,
    val name: String,
    val workingDays: Int,
    val notWorkingDays: Int,
    val shortDays: Int,
    @SerializedName("workingHours") // Здесь находится моя "норма"
    val workingHours: Int
)
