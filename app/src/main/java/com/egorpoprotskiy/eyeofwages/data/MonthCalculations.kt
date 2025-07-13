package com.egorpoprotskiy.eyeofwages.data

import java.math.BigDecimal
import java.math.RoundingMode

fun monthCalculations(month: Month): MonthCalculateData {
    val oneChasDenRub = if (month.norma != 0) month.oklad / month.norma else 0.0
    val oneChasNochRub = oneChasDenRub * 0.4

    val rabTime = month.rabTime * oneChasDenRub
    val nochTime = month.nochTime * oneChasNochRub
    //расчет ночного приказа
    val prikazNochBezDv =
        (month.prikazNoch * oneChasDenRub + month.prikazNoch * oneChasNochRub) + (month.prikazNoch * oneChasDenRub) +
                ((month.prikazNoch * oneChasDenRub + month.prikazNoch * oneChasNochRub) * 0.4)
    val prikazNochDv = (prikazNochBezDv * 0.1 + prikazNochBezDv * 0.2 + prikazNochBezDv * 0.3)
    val prikazNoch = prikazNochBezDv + prikazNochDv
//    val prikazNoch = month.prikazNoch * oneChasDenRub

    val premia = (rabTime + nochTime) * (month.premia / 100.0)
    val prazdTime = month.prazdTime * oneChasDenRub
    val visluga = rabTime * (month.visluga / 100.0)

    val base = rabTime + nochTime + premia + prazdTime + visluga

    val rayon20 = base * 0.2
    val severn30 = base * 0.3
    val rayon10 = base * 0.1

    val itogBezNdfl = base + rayon20 + severn30 + rayon10 + prikazNoch
    val ndfl = itogBezNdfl * 0.13
    val itog = itogBezNdfl - ndfl

    val aliments25 = itog * 0.25
    val aliments75 = itog * 0.75

//Сумма последних 12 месяцев
//    val last12Months = ArrayDeque<Double>()
//    if (last12Months.size >= 12) {
//        last12Months.removeAt(0) //удаляется первый элемент, если их в списке >12
//    }
//    last12Months.add(itog) //Добавляет в список расчитанный итог
//    val sredniyDenInMonth = 29.3 //среднее количество дней в месяце по законодательству РФ
//    val sredniyLast12Months = last12Months.sum() / (12 * sredniyDenInMonth) //средний заработок за последние 12 месяцев
//    val otpuskDen: Int = 1 // количество отпускных дней.
//    val otpusk = sredniyLast12Months * otpuskDen // расчет отпуска


    fun calculateVacation(last12Itogs: List<Double>, otpuskDen: Int): Double {
        val sredniyDenInMonth = 29.3
        val sredniy = last12Itogs.sum() / (12 * sredniyDenInMonth)
        return round2(sredniy * otpuskDen)
    }


    return MonthCalculateData(
        rabTimeRub = round2(rabTime),
        nochTimeRub = round2(nochTime),
        prikazNochRub = round2(prikazNoch),
        premiaRub = round2(premia),
        prazdTimeRub = round2(prazdTime),
        vislugaRub = round2(visluga),
        rayon20 = round2(rayon20),
        severn30 = round2(severn30),
        rayon10 = round2(rayon10),
        ndfl = round2(ndfl),
        itog = round2(itog),
        aliments25 = round2(aliments25),
        aliments75 = round2(aliments75)
    )
}

fun round2(value: Double): Double =
    BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
