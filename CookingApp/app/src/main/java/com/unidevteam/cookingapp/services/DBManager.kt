package com.unidevteam.cookingapp.services

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unidevteam.cookingapp.models.CAUser

class DBManager {

    companion object {
        fun saveUserInfo(userInfo: CAUser) : Task<Void> {
            val db : FirebaseFirestore = FirebaseFirestore.getInstance()
            val usersRef : CollectionReference = db.collection("users")

            val data : HashMap<String, Any> = userInfo.toHashMap()
            return usersRef.document(FirebaseAuth.getInstance().currentUser!!.uid).set(data)
        }
    }
}