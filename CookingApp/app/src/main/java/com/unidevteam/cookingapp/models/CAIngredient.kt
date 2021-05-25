package com.unidevteam.cookingapp.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

data class CAIngredient(val title: String, val category: String, val allergens: Array<String>) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf("title" to title, "category" to category, "allergens" to allergens)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromData(data: Map<String, Any>) : CAIngredient {
            return CAIngredient(
                title = data["title"].toString(),
                category = data["category"].toString(),
                allergens = data["allergens"] as Array<String>
            )
        }
    }
}