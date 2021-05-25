package com.unidevteam.cookingapp.models

data class CARecipe(
    val title: String,
    val ingredients: Map<CAIngredient, Int>,
    val time: Int,
    val difficulty: String,
    val cost: String,
    val chefUID: String
    ) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf(
            "title" to title,
            "ingredients" to ingredients,
            "time" to time,
            "difficulty" to difficulty,
            "cost" to cost,
            "likes" to 0,
            "chefUID" to chefUID
        )
    }
}