package com.unidevteam.cookingapp.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

data class CARecipe(
    val title: String,
    val ingredients: List<CAIngredient>,
    val time: String,
    val difficulty: String,
    val cost: String,
    val process: String,
    val likes: Int = 0,
    val chefUID: String
    ) {

    fun toHashMap() : HashMap<String, Any> {
        val ingredientsJSONs : MutableList<Map<String, Any>> = mutableListOf()
        for(i : Int in ingredients.indices) {
            val ingJSON : Map<String, Any> = ingredients[i].toHashMap()
            ingredientsJSONs.add(ingJSON)
        }

        return hashMapOf(
            "title" to title,
            "ingredients" to ingredientsJSONs,
            "time" to time,
            "difficulty" to difficulty,
            "cost" to cost,
            "process" to process,
            "likes" to likes,
            "chefUID" to chefUID
        )
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromData(data: Map<String, Any>) : CARecipe {
            return CARecipe(
                title = data["title"].toString(),
                ingredients = data["ingredients"] as List<CAIngredient>,
                time = data["time"].toString(),
                difficulty = data["difficulty"].toString(),
                cost = data["cost"].toString(),
                process = data["process"].toString(),
                likes = data["likes"] as? Int?:-1,
                chefUID = data["chefUID"].toString(),
            )
        }

        fun getRecipesData(qSnap: QuerySnapshot) : List<CARecipe> {
            val recipes : MutableList<CARecipe> = mutableListOf()
            for(dSnap : DocumentSnapshot in qSnap.documents) {
                if(dSnap.exists())
                    recipes.add(CARecipe.fromData(dSnap.data!!))
            }
            return recipes
        }
    }
}