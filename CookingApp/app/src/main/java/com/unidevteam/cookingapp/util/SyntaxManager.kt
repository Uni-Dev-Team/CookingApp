package com.unidevteam.cookingapp.util

import java.util.regex.Matcher
import java.util.regex.Pattern

class SyntaxManager {
    companion object {
        // [START password_regex]
        fun passValidator(text: String?):Boolean{
            val pattern: Pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$")
            val matcher: Matcher = pattern.matcher(text)
            return matcher.matches()
        }
        // [END password_regex]

        // [START email_regex]
        fun emailValidator(text: String?):Boolean{
            val pattern: Pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)\$")
            val matcher: Matcher = pattern.matcher(text)
            return matcher.matches()
        }
        // [END email_regex]

        //[START name_regex]
        fun nameValidator(text: String?): Boolean {
            val pattern: Pattern = Pattern.compile("^([a-zA-Z]{2,}\\s[a-zA-Z]{1,}'?-?[a-zA-Z]{1,}\\s?([a-zA-Z]{1,})?)")
            val matcher: Matcher = pattern.matcher(text)
            return matcher.matches()
        }
        //[END name_regex]
    }

}