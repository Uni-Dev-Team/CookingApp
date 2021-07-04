package com.unidevteam.cookingapp.util

class Conversion {
    companion object {
        const val GRAMS = 128.0
        const val CUPS = 0.00496031746
        const val TABLESPOONS = 0.066666666666666666666
        const val CL = 10.0

        // TO GRAMS
        fun cupsToGrams(cups: Double): Double {
            return cups / CUPS
        }

        fun clToGrams(grams: Double): Double {
            return grams * CL
        }

        fun tablespoonsToGrams(tbs: Double): Double {
            return tbs / TABLESPOONS
        }

        // FROM GRAMS
        fun gramsToCl(grams : Double): Double {
            return grams / CL
        }

        fun gramsToCups(grams : Double): Double {
           return grams * CUPS
        }

        fun gramsToTablespoon(grams: Double): Double {
            return grams * TABLESPOONS
        }

        fun tablespoonToCups(tbs: Double): Double {
            return tbs * 0.0625
        }

        fun cupsToTablespoon(cups: Double): Double {
            return cups / 0.0625
        }

        fun clToTablespoon(cl: Double): Double {
            return cl * 0.67628
        }

        fun tablespoonToCl(tbs: Double): Double {
            return tbs / 0.67628
        }

        fun clToCups(cl: Double): Double {
            return cl * 0.0422675
        }

        fun cupsToCl(cups: Double): Double {
            return cups / 0.0422675
        }
    }
}