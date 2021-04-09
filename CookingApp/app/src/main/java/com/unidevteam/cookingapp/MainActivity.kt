package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "Login Activity"
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Firebase
        auth = FirebaseAuth.getInstance()

        // Buttons Listener

        findViewById<Button>(R.id.btn_checkUserStatus).setOnClickListener {
            Log.d(TAG, "Button user status pressed")
            firebaseCheckUserStatus()
        }

        findViewById<TextView>(R.id.lb_registerPage).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btn_logout).setOnClickListener {
            Log.d(TAG, "Button Logout pressed")
            firebaseAuthSignOut()
        }

        findViewById<TextView>(R.id.lb_guest).setOnClickListener {
            Log.d(TAG, "Label guest pressed")
            firebaseAuthAnonymously()
        }

        findViewById<TextView>(R.id.lb_passwordForgotten).setOnClickListener {
            Log.d(TAG, "Password reset pressed")
            firebasePasswordReset(findViewById<TextView>(R.id.tf_email).text.toString())
        }

        findViewById<com.google.android.gms.common.SignInButton>(R.id.btn_googleSignin).setOnClickListener {
            Log.d(TAG, "Button google login pressed")
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            Log.d(TAG, "Button Login pressed")
            if (findViewById<TextView>(R.id.tf_email).text.toString().isNotEmpty() && findViewById<TextView>(R.id.tf_password).text.toString().isNotEmpty()){
                Log.d(TAG, "Text filled")
                firebaseAuthWithEmailAndPassword(findViewById<TextView>(R.id.tf_email).text.toString(), findViewById<TextView>(R.id.tf_password).text.toString())
            } else {
                Log.d(TAG, "Text not filled")
            }
        }
    }


    // Functions

    // [START auth_status]
    private fun firebaseCheckUserStatus() {
        if (auth.currentUser != null) {
            Log.d(TAG, "Sign-in status: Logged \n Info ${auth.currentUser.email.toString()}")
        } else {
            Log.d(TAG, "Sign-in status: Not logged")
        }

    }
    // [START auth_with_email&Password]
    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task->
                if(task.isSuccessful) {
                    Log.d(TAG, "SignIn with email and password: Success!")
                    val user = auth.currentUser
                } else {
                    Log.d(TAG, "SignIn with email and password: Failed - ${task.toString()}")
                }
            }
    }
    // [END auth_with_email&Password]

    // [START auth_anonymously]
    private fun firebaseAuthAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener {task->
                if (task.isSuccessful) {
                    Log.d(TAG, "SignIn anonymously: Success!")
                } else {
                    Log.d(TAG, "SignIn anonymously: Failed - ${task.toString()}")
                }
            }
    }
    // [END auth_anonymously]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
    // [END auth_with_google]

    // [START auth_password_forgotten]
    private fun firebasePasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    // Password reset sent
                    Log.d(TAG, "Email reset: sent")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "Email reset: failure", task.exception)
                }
            }
    }
    // [END auth_password_forgotten]

    // [START auth_sign_out]
    private fun firebaseAuthSignOut() {
        if (auth.currentUser.isAnonymous) { auth.currentUser.delete() }
        auth.signOut()
        if (auth.currentUser == null) {
            Log.d(TAG, "User signed oud")
        } else {
            Log.d(TAG, "Error: User not signed oud")
        }
    }
    // [END auth_sign_out]
}