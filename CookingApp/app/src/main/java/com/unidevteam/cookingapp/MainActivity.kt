package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "Login Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btn_checkUserStatus).setOnClickListener{
            Log.v(TAG, "Button user status pressed")
            checkUserStatus()
        }

        findViewById<TextView>(R.id.lb_registerPage).setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btn_logout).setOnClickListener {
            Log.v(TAG, "Button Logout pressed")
            if (auth.currentUser.isAnonymous) {auth.currentUser.delete()}
            auth.signOut()
            if (auth.currentUser == null) {
                Log.v(TAG, "Sign-out success")
            }
        }

        findViewById<TextView>(R.id.lb_guest).setOnClickListener {
            Log.v(TAG, "Label guest pressed")
            auth.signInAnonymously()
                    .addOnCompleteListener(this){ task->
                        if (task.isSuccessful) {
                            Log.d(TAG, "SignIn anonymously: Success!")
                        } else {
                            Log.d(TAG, "SignIn anonymously: Failed - ${task.toString()}")
                        }
                    }
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            Log.v(TAG, "Button Login pressed")
            if (findViewById<TextView>(R.id.tf_email).text.toString().isNotEmpty() && findViewById<TextView>(R.id.tf_password).text.toString().isNotEmpty()){
                Log.v(TAG, "Text filled")
                auth.signInWithEmailAndPassword(findViewById<TextView>(R.id.tf_email).text.toString(),
                                                findViewById<TextView>(R.id.tf_password).text.toString())
                        .addOnCompleteListener(this) { task->
                            if(task.isSuccessful) {
                                Log.d(TAG, "SignIn with email and password: Success!")
                                val user = auth.currentUser
                            } else {
                                Log.d(TAG, "SignIn with email and password: Failed - ${task.toString()}")
                            }
                        }
            } else {
                Log.v(TAG, "Text not filled")
            }
        }
    }

    private fun checkUserStatus(){
        if(auth.currentUser != null){
            Log.d(TAG, "Sign-in status: Logged \n Info ${auth.currentUser.email}")
        }else{
            Log.d(TAG, "Sign-in status: Not logged")
        }

    }
}