package com.egorpoprotskiy.eyeofwages

import android.app.Application
import com.egorpoprotskiy.eyeofwages.data.AppContainer
import com.egorpoprotskiy.eyeofwages.data.AppDataContainer


//Наследуешься от Application — это точка входа в твое приложение.
//Создаёшь поле container типа AppContainer, которое инициализируешь в onCreate.
//В onCreate создаётся AppDataContainer, куда передаёшь this — контекст приложения.
//Теперь container хранит все зависимости (в данном случае, monthRepository), и он живёт столько же, сколько само приложение.
class MonthApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}