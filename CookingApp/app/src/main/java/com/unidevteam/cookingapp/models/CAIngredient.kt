package com.unidevteam.cookingapp.models

data class CAIngredient(val title: String, val category: String, val allergens: Array<String>) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf("title" to title, "category" to category, "allergens" to allergens)
    }
}