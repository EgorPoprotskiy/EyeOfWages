package com.egorpoprotskiy.eyeofwages.data

import android.content.Context

/*Отлично, ты подошёл к внедрению зависимостей (Dependency Injection) — это то,
как мы централизованно передаём нужные классы (например, Repository) в нужные части приложения.
 */
//Интерфейс описывает контейнер зависимостей — в нашем случае, это то, откуда можно получить MonthRepository.
interface AppContainer {
    val monthRepository: MonthRepository
}


//Здесь ты определяешь реальный способ создания репозитория.
//Используется by lazy { ... }, чтобы OfflineMonthRepository создавался только при первом обращении.
//Получение MonthDatabase.getDatabase(context) — стандартный способ обращения к синглтону Room-базы.
class AppDataContainer(private val context: Context) : AppContainer {
    override val monthRepository: MonthRepository by lazy {
        OfflineMonthRepository(MonthDatabase.getDatabase(context).monthDao())
    }
}