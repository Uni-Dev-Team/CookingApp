package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "Register Activity"

    private fun gotoLoginPage(){
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
           gotoLoginPage()
        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            if (auth.currentUser == null) {
                if (findViewById<TextView>(R.id.tf_email).text.isNotEmpty() && findViewById<TextView>(
                        R.id.tf_password
                    ).text.isNotEmpty() && findViewById<TextView>(R.id.tf_passwordAgain).text.isNotEmpty()) {
                    if (findViewById<TextView>(R.id.tf_password).text.toString() == findViewById<TextView>(
                            R.id.tf_passwordAgain
                        ).text.toString()) {
                        if (findViewById<TextView>(R.id.tf_password).text.length > 5) {
                            auth.createUserWithEmailAndPassword(
                                findViewById<TextView>(R.id.tf_email).text.toString(),
                                findViewById<TextView>(
                                    R.id.tf_password
                                ).text.toString()
                            )
                                    .addOnCompleteListener{ task->
                                        if (task.isSuccessful){
                                            Log.d(TAG, "Registration: User created correctly")
                                            (auth.currentUser as FirebaseUser).sendEmailVerification().addOnCompleteListener { task2->
                                                if (task2.isSuccessful) {
                                                    Log.d(TAG, "Email: sent")
                                                } else {
                                                    Log.d(TAG, "Email: Failed - ${task.exception}")
                                                }
                                            }
                                            auth.signOut()
                                            gotoLoginPage()
                                        } else {
                                            Log.d(TAG, "Registration: Failed - ${task.exception}")
                                        }
                                    }
                        } else {
                            Log.d(TAG, "Error: Password too short")
                        }
                    } else {
                        Log.d(TAG, "Error: Password mismatch")
                    }
                } else {
                    Log.d(TAG, "Error: Field empty")
                }

            } else {
                Log.d(TAG, "Error: User already logged")
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}