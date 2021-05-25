package com.unidevteam.cookingapp.models

data class CAReview(val recipeID: String, val authorUID: String, val title: String, val description: String, val score: Int) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf(
            "recipeID" to recipeID,
            "authorID" to authorUID,
            "title" to title,
            "description" to description,
            "score" to score
        )
    }
}