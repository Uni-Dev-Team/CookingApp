package com.unidevteam.cookingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // User signed in
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            if (findViewById<TextView>(R.id.tf_email).text.toString().isNotEmpty() && findViewById<TextView>(R.id.tf_password).text.toString().isNotEmpty()){
                Log.v(TAG, "Text filled")
                auth.signInWithEmailAndPassword(findViewById<TextView>(R.id.tf_email).text.toString(),
                                                findViewById<TextView>(R.id.tf_password).text.toString())
                        .addOnCompleteListener(this) { task->
                            if(task.isSuccessful) {
                                Log.d(TAG, "SignIn with email and password: Success")
                                val user = auth.currentUser
                            } else {
                                Log.d(TAG, "SignIn with email and password: Failure")
                                Toast.makeText(baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
            } else {
                Log.v(TAG, "Text not filled")
            }
            findViewById<TextView>(R.id.btn_logout).setOnClickListener {
                Firebase.auth.signOut()
                if (Firebase.auth.currentUser != null) {
                    Log.v(TAG, "Sign-out success")
                }
            }
        }
    }
}