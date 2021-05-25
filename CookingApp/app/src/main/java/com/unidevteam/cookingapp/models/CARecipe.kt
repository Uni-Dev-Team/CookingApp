package com.unidevteam.cookingapp.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

data class CARecipe(
    val title: String,
    val ingredients: List<CAIngredient>,
    val amounts: List<Int>,
    val time: Int,
    val difficulty: String,
    val cost: String,
    val likes: Int = 0,
    val chefUID: String
    ) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf(
            "title" to title,
            "ingredients" to ingredients,
            "amounts" to amounts,
            "time" to time,
            "difficulty" to difficulty,
            "cost" to cost,
            "likes" to likes,
            "chefUID" to chefUID
        )
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromData(data: Map<String, Any>) : CARecipe {
            return CARecipe(
                title = data["title"].toString(),
                ingredients = data["ingredients"] as List<CAIngredient>, // Da modificare
                amounts = data["amounts"] as List<Int>,
                time = data["time"] as? Int?:-1,
                difficulty = data["difficulty"].toString(),
                cost = data["cost"].toString(),
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