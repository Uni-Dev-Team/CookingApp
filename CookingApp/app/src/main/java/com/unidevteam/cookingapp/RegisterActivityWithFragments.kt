package com.unidevteam.cookingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit

class RegisterActivityWithFragments : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_with_fragments)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<RegisterStep1>(R.id.fragment_container_view_registration)
            }
        }
    }
    companion object {
        private const val TAG = "RegisterActivity"
    }
}