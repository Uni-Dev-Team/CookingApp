package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val NUM_PAGES = 2

class RegisterActivityWithFragments : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var viewPager: ViewPager2

    private lateinit var _name : String
    private lateinit var _email : String
    private lateinit var _password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_with_fragments)

        auth = FirebaseAuth.getInstance()

        viewPager = findViewById(R.id.fragment_container_view_registration)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            if(viewPager.currentItem == 0) {
                val name = findViewById<TextView>(R.id.tf_name).text.toString()
                val email = findViewById<TextView>(R.id.tf_email).text.toString()
                // Da aggiungere:
                // - Log grafico per errori e avvisi all'utente
                if(name.isNotEmpty() && email.isNotEmpty() && nameValidator(name) && emailValidator(email)) {
                    Log.d(TAG, "Nome: $name")
                    Log.d(TAG, "Email: $email")
                    viewPager.setCurrentItem(1)
                    _name = name
                    _email = email
                    findViewById<Button>(R.id.btn_next).setText(R.string.signup_button)
                }
            } else if(viewPager.currentItem == 1) {
                val password1 = findViewById<TextView>(R.id.tf_password_1).text.toString()
                val password2 = findViewById<TextView>(R.id.tf_password_2).text.toString()
                // Da aggiungere:
                // - Log grafico per errori e avvisi all'utente
                if(password1.isNotEmpty() && password2.isNotEmpty() && password1.equals(password2) && passValidator(password1)) {
                    Log.d(TAG, "Password 1: $password1")
                    Log.d(TAG, "Password 2: $password2")
                    _password = password1

                    if (auth.currentUser == null) {
                        firebaseCreateUser(_email, _password)
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
                    } else { Log.d(TAG, "Error: User already logged")
                        gotoLoginPage()
                    }
                }
            }
        }

        findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
            gotoLoginPage()
        }
    }

    override fun onBackPressed() {
        if(viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
            findViewById<Button>(R.id.btn_next).setText(R.string.avanti)
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment = if(position==0) RegisterStep1() else RegisterStep2()
    }

    // [START goto_login_page]
    private fun gotoLoginPage(){
        val intent = Intent(this, MainActivity::class.java)
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
    private fun firebaseCreateUser(email: String, password: String) : Task<AuthResult> {
        return auth.createUserWithEmailAndPassword( email, password)
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