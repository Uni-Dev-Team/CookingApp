package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {
    private val user: FirebaseUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        if (user != null) {
            findViewById<TextView>(R.id.lb_profileId).text = "Profile ID: ${user.uid}"
            findViewById<TextView>(R.id.lb_displayName).text = "Display Name: ${user.displayName}"
            findViewById<TextView>(R.id.lb_email).text = "Email: ${user.email}"
            findViewById<TextView>(R.id.lb_isVerified).text = "Is Verified: ${user.isEmailVerified.toString()}"
        } else {
            gotoLoginPage()
        }

        findViewById<TextView>(R.id.btn_logout).setOnClickListener {
            Log.d(TAG, "Button Logout pressed")
            firebaseAuthSignOut()
            gotoLoginPage()
        }

    }
    // [START auth_sign_out]
    private fun firebaseAuthSignOut() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        if (auth.currentUser.isAnonymous) { auth.currentUser.delete() }
        auth.signOut()
        if (auth.currentUser == null) {
            Log.d(TAG, "User signed oud")
        } else {
            Log.d(TAG, "Error: User not signed oud")
        }
    }
    // [END auth_sign_out]

    // [START goto_login_page]
    private fun gotoLoginPage(){
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]

    companion object {
        private const val TAG = "ProfileActivity"
    }
}