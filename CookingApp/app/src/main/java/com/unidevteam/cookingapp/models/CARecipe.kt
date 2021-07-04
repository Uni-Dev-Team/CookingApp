package com.unidevteam.cookingapp.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
data class CARecipe(
    var recipeID: String,
    var imageURL: String?,
    val title: String,
    val ingredients: List<CAIngredient>,
    val time: String,
    val difficulty: String,
    val cost: String,
    val process: String,
    val numOfPerson: Int,
    val likes: Int = 0,
    val chefUID: String,
    val typePortata: String,
    val typology: String
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
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeStringArray(arrayOf(recipeID, imageURL, title, time, difficulty, cost, process, numOfPerson.toString(), likes.toString(), chefUID, typePortata, typology))
    }

    fun toHashMap() : HashMap<String, Any> {
        val ingredientsJSONs : MutableList<Map<String, Any>> = mutableListOf()
        for(i : Int in ingredients.indices) {
            val ingJSON : Map<String, Any> = ingredients[i].toHashMap()
            ingredientsJSONs.add(ingJSON)
        }

        return hashMapOf(
            "recipeID" to recipeID,
            "imageURL" to imageURL!!,
            "title" to title,
            "ingredients" to ingredientsJSONs,
            "time" to time,
            "difficulty" to difficulty,
            "cost" to cost,
            "process" to process,
            "numOfPerson" to numOfPerson,
            "likes" to likes,
            "chefUID" to chefUID,
            "typePortata" to typePortata,
            "typology" to typology
        )
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
                recipeID = data["recipeID"].toString(),
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
                typePortata = data["typePortata"].toString(),
                typology = data["typology"].toString()
            )
        }

        fun toGroceryList(ings: MutableList<CAIngredient>) : String {
            var groceryList = StringBuilder()
            for(ingredient : CAIngredient in ings) {
                val newLine : String = System.getProperty("line.separator")
                groceryList.append("${ingredient.name} - ${ingredient.amount}${ingredient.unit}$newLine")
            }

            return groceryList.toString().trim()
        }

        private val TAG = "Recipe"
    }
}