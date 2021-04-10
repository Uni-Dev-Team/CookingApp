package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
           gotoLoginPage()
        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            if (auth.currentUser == null) {
                if (findViewById<TextView>(R.id.tf_email).text.isNotEmpty() && findViewById<TextView>(R.id.tf_password).text.isNotEmpty() && findViewById<TextView>(R.id.tf_passwordAgain).text.isNotEmpty()) {
                    if (emailValidator(findViewById<TextView>(R.id.tf_email).text.toString())) {
                        if (findViewById<TextView>(R.id.tf_password).text.toString() == findViewById<TextView>(R.id.tf_passwordAgain).text.toString()) {
                            if (passValidator(findViewById<TextView>(R.id.tf_password).text.toString())) {
                                firebaseCreateUser(findViewById<TextView>(R.id.tf_email).text.toString(), findViewById<TextView>(R.id.tf_password).text.toString())
                            } else {
                                Log.d(TAG, "Error: Password Regex mismatch")
                            }
                        } else {
                            Log.d(TAG, "Error: Password mismatch")
                        }
                    } else {
                        Log.d(TAG, "Error: Email Regex mismatch")
                    }
                } else {
                    Log.d(TAG, "Error: Field empty")
                }

            } else {
                Log.d(TAG, "Error: User already logged")
                gotoLoginPage()
            }
        }
    }
    // [START goto_login_page]
    private fun gotoLoginPage(){
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]

    // [START password_regex]
    private fun passValidator(text: String?):Boolean{
        val pattern: Pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$ %^&*-]).{8,}\$")
        val matcher: Matcher = pattern.matcher(text)
        return matcher.matches()
    }
    // [END password_regex]

    // [START email_regex]
    private fun emailValidator(text: String?):Boolean{
        val pattern: Pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)\$")
        val matcher: Matcher = pattern.matcher(text)
        return matcher.matches()
    }
    // [END email_regex]

    // [START create_user]
    private fun firebaseCreateUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword( email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Registration: User created correctly")
                        firebaseSendPasswordVerification(auth.currentUser)
                        auth.signOut()
                        gotoLoginPage()
                    } else {
                        Log.d(TAG, "Registration: Failed - ${task.exception}")
                    }
                }
    }
    // [END create_user]

    // [START send_verification_email]
    private fun firebaseSendPasswordVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Email: sent")
            } else {
                Log.d(TAG, "Email: Failed - ${task.exception}")
            }
        }
    }
    // [END send_verification_email]

    companion object {
        private const val TAG = "RegisterActivity"
    }
}