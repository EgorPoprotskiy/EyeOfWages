package com.egorpoprotskiy.eyeofwages.data

data class MonthCalculateData(
    val rabTimeRub: Double = 0.0,
    val nochTimeRub: Double = 0.0,
    val prikazDenRub: Double = 0.0,
    val prikazNochRub: Double = 0.0,
    val premiaRub: Double = 0.0,
    val prazdTimeRub: Double = 0.0,
    val vislugaRub: Double = 0.0,
    val rayon20: Double = 0.0,
    val severn30: Double = 0.0,
    val rayon10: Double = 0.0,
    val ndfl: Double = 0.0,
    val itog: Double = 0.0,
    val aliments25: Double = 0.0,
    val aliments75: Double = 0.0,
    val itogBezNdfl: Double = 0.0,

    val bolnichniy: Double = 0.0, // Сумма, исключаемая из СДЗ (Больничные, пособия)
    val otpuskDays: Double = 0.0, // Дни отпуска (если ввод <= 39)
    val otpuskPay: Double = 0.0 // Сумма отпускных (введенная или рассчитанная)
)
