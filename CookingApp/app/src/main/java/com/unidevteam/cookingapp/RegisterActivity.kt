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

//TODO change layout in order to space evenly between UI components

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
           gotoLoginPage()
        }

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            if (auth.currentUser == null) {
                if (findViewById<TextView>(R.id.tf_email).text.isNotEmpty() && findViewById<TextView>(
                        R.id.tf_name
                    ).text.isNotEmpty()
                ) {
                    // TODO is it better to merge these two if statements for better error handling?
                    if (emailValidator(findViewById<TextView>(R.id.tf_email).text.toString())) {
                        if (nameValidator(findViewById<TextView>(R.id.tf_name).text.toString())) {
                            // Go next activity
                            // val intent = Intent(this, activityName::class.java)

                            Log.d(TAG, "Registered Successfully")


                        } else {
                            Log.d(TAG, "Error: Name Regex mismatch")
                        }
                    } else {
                            Log.d(TAG, "Error: Email Regex mismatch")
                        }
                } else {
                    Log.d(TAG, "Error: Field empty")
                }
            } else { Log.d(TAG, "Error: User already logged")
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
    // TODO: pass validation is never used, bring this to register pt 2
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

    //[START] name_regex
    private fun nameValidator(text: String?): Boolean {
        val pattern: Pattern = Pattern.compile("^([a-zA-Z]{2,}\\s[a-zA-Z]{1,}'?-?[a-zA-Z]{1,}\\s?([a-zA-Z]{1,})?)")
        val matcher: Matcher = pattern.matcher(text)
        return matcher.matches()
    }
    //[END] name_regex

    // [START create_user]
    // TODO bring this to registration pt 2 because it's never used
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
        const val TAG = "RegisterActivity"
    }
}