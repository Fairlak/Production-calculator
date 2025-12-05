package com.example.calculator

import com.example.calculator.storage.calculate.InputData
import kotlin.math.sqrt

const val R_d = 287.05
const val R_v = 461.495


class FormulaCalc(val data: InputData) {

    fun waterPressure(): Double{
        val tempC = data.tempCelsius
        val wet = data.relativeHumidity

        val exponent = (17.625 * tempC) / (tempC + 243.04)
        val P_sT_hPa = 6.1094 * Math.exp(exponent)
        val Pv_hPa = (wet / 100.0) * P_sT_hPa
        val Pv_pascals = Pv_hPa * 100.0
        return Pv_pascals
    }

    fun dryAirPressure(): Double{
        val atmPressure_hPa = data.atmPressure
        val statPressure_Pa = data.statPressure


        val Pv_pascals = waterPressure()

        val P_total_Pa = (atmPressure_hPa * 100.0) + statPressure_Pa
        val P_dryAir = P_total_Pa - Pv_pascals

        return P_dryAir
    }

    fun airDensityCalculation(): Double{
        val tempK = data.tempCelsius + 273.15

        val P_dryAir = dryAirPressure()
        val Pv_pascals = waterPressure()

        val term_dry = P_dryAir / (R_d * tempK)
        val term_vapor = Pv_pascals / (R_v * tempK)

        val densityAir = term_dry + term_vapor

        return densityAir

    }

    fun consumption(manufacturer: String?): Double{
        val caliberFactor = data.calibrationFactor
        val pressureDrop = data.pressureDrop
        val densityCalculation = airDensityCalculation()

        return when (manufacturer) {
            "FläktWoods" -> (1/caliberFactor) * sqrt(pressureDrop)
            "Ziehl", "ebm-papst", "Nicotra", "Common probe (e.g. FloXact)" -> caliberFactor * sqrt(pressureDrop)
            else -> caliberFactor * sqrt((2 / densityCalculation) * pressureDrop)
        }



    }

}