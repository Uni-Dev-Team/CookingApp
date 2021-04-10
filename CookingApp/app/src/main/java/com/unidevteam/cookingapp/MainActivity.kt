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
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
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

        findViewById<TextView>(R.id.lb_registerPage).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
            firebaseSignInWithGoogle()
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            Log.d(TAG, "Button Login pressed")
            if (findViewById<TextView>(R.id.tf_email).text.toString().isNotEmpty() && findViewById<TextView>(R.id.tf_password).text.toString().isNotEmpty()){
                Log.d(TAG, "Text filled")
                firebaseAuthWithEmailAndPassword(findViewById<TextView>(R.id.tf_email).text.toString(), findViewById<TextView>(R.id.tf_password).text.toString())
                if (firebaseCheckUserStatus()) {
                    gotoProfilePage()
                } else {
                    Log.d(TAG, "Not logged")
                }
            } else {
                Log.d(TAG, "Text not filled")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            gotoProfilePage()
        }
    }
    // Functions

    // [START goto_login_page]
    private fun gotoProfilePage(){
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]

    // [START auth_status]
    private fun firebaseCheckUserStatus(): Boolean {
        return if (auth.currentUser != null) {
            Log.d(TAG, "Sign-in status: Logged \n Info ${auth.currentUser.email.toString()}")
            true
        } else {
            Log.d(TAG, "Sign-in status: Not logged")
            false
        }

    }
    // [START auth_with_email&Password]
    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task->
                if(task.isSuccessful) {
                    Log.d(TAG, "SignIn with email and password: Success!")
                } else {
                    Log.d(TAG, "SignIn with email and password: Failed - ${task.toString()}")
                }
            }
    }
    // [END auth_with_email&Password]

    // [START auth_anonymously]
    private fun firebaseAuthAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task->
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
                        gotoProfilePage()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
    }

    private fun firebaseSignInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END auth_with_google]

    // [START auth_with_facebook]

    // [END auth_with_facebook]

    // [START auth_password_forgotten]
    private fun firebasePasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    // Password reset sent
                    Log.d(TAG, "Email reset: sent")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "Email reset: failure", task.exception)
                }
            }
    }
    // [END auth_password_forgotten]


    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }
}