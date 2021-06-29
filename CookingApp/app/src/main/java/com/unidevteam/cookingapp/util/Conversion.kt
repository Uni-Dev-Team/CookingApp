package com.unidevteam.cookingapp.util

class Conversion {
    fun gramsToCups(grams : Double): Double {
       return grams / GRAMS
   }
    fun cupsToGrams(cups: Double): Double {
        return cups * GRAMS
    }
    fun gramsToOunces(grams: Double): Double {
        return (OUNCES * grams) / GRAMS
    }
    fun ouncesToGrams(ounces: Double): Double {
        return  (GRAMS * ounces) / OUNCES
    }
    fun ouncesToCups(ounces: Double): Double {
        return  ounces / OUNCES
    }
    fun cupsToOunces(cups: Double): Double {
        return cups * OUNCES
    }
    fun cupsToTablespoons(cups: Double): Double {
        return cups * TABLESPOONS
    }
    fun tablespoonsToCups(tablespoons: Double): Double {
        return tablespoons / TABLESPOONS
    }


    companion object {
        const val GRAMS = 128
        const val OUNCES = 4.5
        const val CUPS = 1
        const val TABLESPOONS = 16
    }
}