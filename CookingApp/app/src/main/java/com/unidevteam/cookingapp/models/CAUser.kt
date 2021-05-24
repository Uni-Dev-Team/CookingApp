package com.unidevteam.cookingapp.models

data class CAUser(val email: String, val name: String, val surname: String, val bio: String) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf("email" to email, "name" to name, "surname" to surname, "bio" to bio)
    }
}