package com.egorpoprotskiy.eyeofwages

import android.app.Application
import com.egorpoprotskiy.eyeofwages.data.AppContainer
import com.egorpoprotskiy.eyeofwages.data.AppDataContainer

class MonthApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}