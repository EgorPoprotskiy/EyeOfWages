package com.egorpoprotskiy.eyeofwages.data

import android.content.Context

interface AppContainer {
    val monthRepository: MonthRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val monthRepository: MonthRepository by lazy {
        OfflineMonthRepository(MonthDatabase.getDatabase(context).monthDao())
    }
}