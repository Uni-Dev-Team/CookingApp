package com.unidevteam.cookingapp.services

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.models.CAUser

class DBManager {
    companion object {
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val usersRef : CollectionReference = db.collection("users")
        val recipesRef : CollectionReference = db.collection("recipes")

        // User related methods
        fun saveUserInfo(userInfo: CAUser) : Task<Void> {
            val data : HashMap<String, Any> = userInfo.toHashMap()
            return usersRef.document(FirebaseAuth.getInstance().currentUser!!.uid).set(data)
        }

        fun getCurrentUserExtraInfo() : Task<DocumentSnapshot> {
            return usersRef.document(FirebaseAuth.getInstance().currentUser!!.uid).get()
        }

        fun getUserInfoFromUID(uid: String) : Task<DocumentSnapshot> {
            return usersRef.document(uid).get()
        }

        fun genRecipeDocumentID() : String {
            return recipesRef.document().id
        }

        // Recipes related methods
        fun getRecipesData(batchSize: Long) : Task<QuerySnapshot> {
            // Get recipes of current user
            // return recipesRef.whereEqualTo("chefUID", FirebaseAuth.getInstance().currentUser!!.uid).get()

            // Get all recipes
            return recipesRef.limit(batchSize).get()
        }

        fun addNewRecipe(recipe: CARecipe) : Task<Void> {
            val recipeMap : Map<String, Any> = recipe.toHashMap()

            return recipesRef.document(recipe.recipeID).set(recipeMap)
        }

        fun getRecipe(recipe: CARecipe) : Task<QuerySnapshot> {
            Log.e("DBManager", FirebaseAuth.getInstance().currentUser!!.uid)
            return recipesRef.whereEqualTo("chefUID", FirebaseAuth.getInstance().currentUser!!.uid).whereEqualTo("title", recipe.title).get()
        }

        fun removeRecipe(recipeDocumentId: String) : Task<Void> {
            return recipesRef.document(recipeDocumentId).delete()
        }

        // Reviews related methods

    }
}