package com.unidevteam.cookingapp.models

data class CAIngredient(val name: String, val amount: String, val unit: String) {

    fun toHashMap() : HashMap<String, Any> {
        return hashMapOf("name" to name, "amount" to amount, "unit" to unit)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromData(data: Map<String, Any>) : CAIngredient {
            return CAIngredient(
                name = data["name"].toString(),
                amount = data["amount"].toString(),
                unit = data["unit"].toString()
            )
        }
    }
}