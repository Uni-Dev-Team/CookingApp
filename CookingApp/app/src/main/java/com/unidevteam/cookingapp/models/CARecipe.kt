package com.unidevteam.cookingapp.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
data class CARecipe(
    var imageURL: String?,
    val title: String,
    val ingredients: List<CAIngredient>,
    val time: String,
    val difficulty: String,
    val cost: String,
    val process: String,
    val numOfPerson: Int,
    val likes: Int = 0,
    val chefUID: String
    ) : Parcelable {
    var ingredientsString : String

    init {
        val res = StringBuilder()

        for(ingredient: CAIngredient in ingredients) {
            res.append("${ingredient.name} ")
        }

        ingredientsString = res.toString()
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    fun toHashMap() : HashMap<String, Any> {
        val ingredientsJSONs : MutableList<Map<String, Any>> = mutableListOf()
        for(i : Int in ingredients.indices) {
            val ingJSON : Map<String, Any> = ingredients[i].toHashMap()
            ingredientsJSONs.add(ingJSON)
        }

        return hashMapOf(
            "imageURL" to imageURL!!,
            "title" to title,
            "ingredients" to ingredientsJSONs,
            "time" to time,
            "difficulty" to difficulty,
            "cost" to cost,
            "process" to process,
            "numOfPerson" to numOfPerson,
            "likes" to likes,
            "chefUID" to chefUID
        )
    }

    // TODO: modificare la lista della spesa per ottenere anche le porzioni
    fun toGroceryList() : String {
        var groceryList = StringBuilder()
        for(ingredient : CAIngredient in ingredients){
            groceryList.append("${ingredient}\n")
        }
        return groceryList.toString().trim()
    }

    override fun toString() : String {
        return "Image URL: $imageURL\nTitle: $title\nIngredients count: ${ingredients.size}\ntime: $time\ndifficulty: $difficulty\ncost: $cost\nprocess: $process\nnumOfPerson: $numOfPerson"
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromData(data: Map<String, Any>) : CARecipe {
            val ings : MutableList<CAIngredient> = mutableListOf()
            for(ing : Map<String, Any> in (data["ingredients"] as List<Map<String, Any>>)) {
                ings.add(CAIngredient.fromData(ing))
            }

            return CARecipe(
                imageURL = data["imageURL"].toString(),
                title = data["title"].toString(),
                ingredients = ings,
                time = data["time"].toString(),
                difficulty = data["difficulty"].toString(),
                cost = data["cost"].toString(),
                process = data["process"].toString(),
                numOfPerson = Integer.parseInt(data["numOfPerson"].toString()),
                likes = Integer.parseInt(data["likes"].toString()),
                chefUID = data["chefUID"].toString(),
            )
        }

        private val TAG = "Recipe"
    }
}