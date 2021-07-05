package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unidevteam.cookingapp.activities.Signup.RegisterStep1
import com.unidevteam.cookingapp.activities.Signup.RegisterStep2
import com.unidevteam.cookingapp.models.CAUser
import com.unidevteam.cookingapp.services.DBManager
import com.unidevteam.cookingapp.util.SyntaxManager

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var viewPager: ViewPager2

    private lateinit var _name : String
    private lateinit var _email : String
    private lateinit var _password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        viewPager = findViewById(R.id.fragment_container_view_registration)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            if(viewPager.currentItem == 0) {
                val name = findViewById<TextView>(R.id.tf_name).text.toString()
                val email = findViewById<TextView>(R.id.tf_email).text.toString()
                if(name.isNotEmpty() && email.isNotEmpty()) {
                    if (SyntaxManager.nameValidator(name) && SyntaxManager.emailValidator(email)) {
                        Log.d(TAG, "Nome: $name")
                        Log.d(TAG, "Email: $email")
                        viewPager.currentItem = 1
                        _name = name
                        _email = email
                        findViewById<Button>(R.id.btn_next).setText(R.string.signup_button)
                    } else {
                        Toast.makeText(this, "Name/email syntax not correct", Toast.LENGTH_SHORT)
                            .show()
                    }
                }else {
                    Toast.makeText(this, "Name/email not filled", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if(viewPager.currentItem == 1) {
                val password1 = findViewById<TextView>(R.id.tf_password_1).text.toString()
                val password2 = findViewById<TextView>(R.id.tf_password_2).text.toString()
                if(password1.isNotEmpty() && password2.isNotEmpty() && SyntaxManager.passValidator(
                        password1
                    )
                ) {
                    if(password1 == password2) {
                        Log.d(TAG, "Password 1: $password1")
                        Log.d(TAG, "Password 2: $password2")
                        _password = password1

                        if (auth.currentUser == null) {
                            firebaseCreateUser(_email, _password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Firebase.storage.reference.child("profilePics/defaultProfile.png").downloadUrl
                                            .addOnCompleteListener { urlTask ->
                                                Log.d(TAG, "Registration: User created correctly")
                                                auth.currentUser?.let { it1 ->
                                                    firebaseSendPasswordVerification(
                                                        it1
                                                    )
                                                }

                                                Log.d(TAG, "Email inserita: $_email")
                                                Log.d(TAG, "Nome inserito: ${_name.split(' ')[0]}")
                                                Log.d(TAG, "Cognome inserito: ${_name.split(' ')[1]}")

                                                val caUser : CAUser = CAUser(
                                                    imageURL = urlTask.result.toString(),
                                                    email = _email,
                                                    name = _name.split(' ')[0],
                                                    surname = _name.split(' ')[1],
                                                    bio = "Ciao! Sono un nuovo cuoco di CookingApp!"
                                                )

                                                DBManager.saveUserInfo(caUser)
                                                    .addOnCompleteListener {
                                                        Log.d(TAG, "Dati extra dell'utente salvati correttamente")

                                                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(_name).setPhotoUri(urlTask.result).build()

                                                        auth.currentUser!!.updateProfile(profileUpdates)
                                                            .addOnCompleteListener { task2 ->
                                                                if(task2.isSuccessful) {
                                                                    Log.d(TAG, "Log: Display-name set successively set")
                                                                    auth.signOut()
                                                                    gotoLoginPage()
                                                                } else {
                                                                    Log.d(
                                                                        TAG,
                                                                        "Error: Display-name set successively failed"
                                                                    )
                                                                }
                                                            }
                                                    }
                                                    .addOnFailureListener {
                                                        Log.d(TAG, "Errore nel salvataggio dei dati extra dell'utente")
                                                    }
                                            }
                                    } else {
                                        Log.d(TAG, "Registration: Failed - ${task.exception}")
                                        Toast.makeText(this, "User already taken", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Log.d(TAG, "Error: User already logged")
                            gotoLoginPage()
                        }
                    } else {
                        Toast.makeText(this, "Le password non combaciano", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Formato password non valido", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]



    // [START create_user]
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
        private const val NUM_PAGES = 2
    }
}