package com.unidevteam.cookingapp.models

data class CAUser(val imageURL : String, val email: String, val name: String, val surname: String, val bio: String) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf("imageURL" to imageURL, "email" to email, "name" to name, "surname" to surname, "bio" to bio)
    }

    companion object {
        fun fromData(data : Map<String, Any>) : CAUser {
            return CAUser(
                imageURL = data["imageURL"].toString(),
                email = data["email"].toString(),
                name = data["name"].toString(),
                surname = data["surname"].toString(),
                bio = data["bio"].toString()
            )
        }
    }
}