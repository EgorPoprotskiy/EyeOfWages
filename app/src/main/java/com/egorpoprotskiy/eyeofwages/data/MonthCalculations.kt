package com.egorpoprotskiy.eyeofwages.data

import java.math.BigDecimal
import java.math.RoundingMode

fun calculateMonthData(month: Month): MonthCalculateData {
    val oneChasDenRub = if (month.norma != 0) month.oklad / month.norma else 0.0
    val oneChasNochRub = oneChasDenRub * 0.4

    val rabTime = month.rabTime * oneChasDenRub
    val nochTime = month.nochTime * oneChasNochRub
    val prikazNoch = month.prikaz * oneChasDenRub
    val premia = (rabTime + nochTime + prikazNoch) * (month.premia / 100.0)
    val prazdTime = month.prazdTime * oneChasDenRub
    val visluga = month.oklad * (month.visluga / 100.0)

    val base = rabTime + nochTime + prikazNoch + premia + prazdTime + visluga

    val rayon20 = base * 0.2
    val severn30 = base * 0.3
    val rayon10 = base * 0.1

    val itogBezNdfl = base + rayon20 + severn30 + rayon10
    val ndfl = itogBezNdfl * 0.13
    val itog = itogBezNdfl - ndfl

    return MonthCalculateData(
        rabTimeRub = round2(rabTime),
        nochTimeRub = round2(nochTime),
        prikazRub = round2(prikazNoch),
        premiaRub = round2(premia),
        prazdTimeRub = round2(prazdTime),
        vislugaRub = round2(visluga),
        rayon20 = round2(rayon20),
        severn30 = round2(severn30),
        rayon10 = round2(rayon10),
        ndfl = round2(ndfl),
        itog = round2(itog)
    )
}

fun round2(value: Double): Double =
    BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
