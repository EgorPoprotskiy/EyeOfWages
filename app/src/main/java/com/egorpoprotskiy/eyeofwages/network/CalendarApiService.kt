package com.egorpoprotskiy.eyeofwages.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Новый конвертер Gson
interface CalendarApiService {
    @GET("api/calendar/{year}/{month}") // Добавили "api/calendar/" в путь
    suspend fun getMonthCalendarData(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Response<CalendarResponse> // Теперь ожидаем CalendarResponse
}

object CalendarApi {
    private const val BASE_URL = "https://calendar.kuzyak.in/"

    val retrofitService: CalendarApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // Важно: используем Gson
            .baseUrl(BASE_URL)
            .build()
            .create(CalendarApiService::class.java)
    }
}