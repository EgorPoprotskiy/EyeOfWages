package com.egorpoprotskiy.eyeofwages.data

import java.math.BigDecimal
import java.math.RoundingMode

fun MonthCalculations(
    month: Month
): MonthCalculateData {
    //Для отпуска
    val bolnichniy = month.bolnichniy

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

    //Расчет дневного приказа
    val prikazDenBezDv =
        (month.prikazDen * oneChasDenRub + month.prikazDen * oneChasDenRub) +
                (month.prikazDen * oneChasDenRub * 0.4)
    val prikazDenDv = (prikazDenBezDv * 0.1 + prikazDenBezDv * 0.2 + prikazDenBezDv * 0.3)
    val prikazDen = prikazDenBezDv + prikazDenDv

    val premia = (rabTime + nochTime) * (month.premia / 100.0)
    val prazdTime = month.prazdTime * oneChasDenRub

    //Расчет выслуги лет
    val visluga = (rabTime + (month.prikazDen * oneChasDenRub) + (month.prikazNoch * oneChasDenRub)) * (month.visluga / 100.0)

    val base = rabTime + nochTime + premia + prazdTime + visluga

    val rayon20 = base * 0.2
    val severn30 = base * 0.3
    val rayon10 = base * 0.1

//    val itogBezNdfl = base + rayon20 + severn30 + rayon10 + prikazNoch + prikazDen // для расчета дохода за 12 месяцев
    val itogBezNdfl = base + rayon20 + severn30 + rayon10 + prikazNoch + prikazDen
//    otpuskPay = otpuskPay - (otpuskPay * 0.13) //вычитаем налог для отображения на экране деталей
    val ndfl = itogBezNdfl * 0.13 //расчет налога со всех доходов.
    val itog = itogBezNdfl - ndfl //итоговая сумма вместе с отпускными(после налогов)

    val aliments25 = itog * 0.25
    val aliments75 = itog * 0.75

    fun calculateVacation(last12Itogs: List<Double>, otpuskDen: Int): Double {
        val sredniyDenInMonth = 29.3
        val sredniy = last12Itogs.sum() / (12 * sredniyDenInMonth)
        return round2(sredniy * otpuskDen)
    }

    return MonthCalculateData(
        rabTimeRub = round2(rabTime),
        nochTimeRub = round2(nochTime),
        prikazDenRub = round2(prikazDen),
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
        aliments75 = round2(aliments75),
        itogBezNdfl =  round2(itogBezNdfl),

        bolnichniy = round2(bolnichniy), // Сумма, исключаемая из СДЗ (Больничные, пособия)
    )
}

fun round2(value: Double): Double =
    BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
