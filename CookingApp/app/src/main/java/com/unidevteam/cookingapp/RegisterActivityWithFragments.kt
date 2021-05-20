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

private const val NUM_PAGES = 2

class RegisterActivityWithFragments : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    private lateinit var _name : String
    private lateinit var _email : String
    private lateinit var _password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_with_fragments)

        viewPager = findViewById(R.id.fragment_container_view_registration)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            if(viewPager.currentItem == 0) {
                val name = findViewById<TextView>(R.id.tf_name).text.toString()
                val email = findViewById<TextView>(R.id.tf_email).text.toString()
                // Da aggiungere:
                // - Controlli con regex per formato email e nome
                // - Log grafico per errori e avvisi all'utente
                if(name.isNotEmpty() && email.isNotEmpty()) {
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
                // - Controlli con regex sul formato delle password
                // - Log grafico per errori e avvisi all'utente
                if(password1.isNotEmpty() && password2.isNotEmpty() && password1.equals(password2)) {
                    Log.d(TAG, "Password 1: $password1")
                    Log.d(TAG, "Password 2: $password2")
                    _password = password1
                }
            }
        }

        /*if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RegisterStep1>(R.id.fragment_container_view_registration)
            }
        }*/
        findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    companion object {
        private const val TAG = "RegisterActivity"
    }
}