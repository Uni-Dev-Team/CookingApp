package com.unidevteam.cookingapp.activities.Home

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.unidevteam.cookingapp.R

class HomeActivity : AppCompatActivity() {
    val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        val homeFragment = HomeFragment()
        val addRecipeFragment = if (FirebaseAuth.getInstance().currentUser!!.isAnonymous) NotAllowedFragment() else  AddRecipeFragment()
        val profileFragment = if (FirebaseAuth.getInstance().currentUser!!.isAnonymous) NotAllowedFragment() else  ProfileFragment()
        val conversionFragment = ConversionFragment()

        setCurrentFragment(homeFragment)

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> {
                    setCurrentFragment(homeFragment)
                }
                R.id.nav_add_recipe -> {
                    setCurrentFragment(addRecipeFragment)
                }
                R.id.nav_profile -> {
                    setCurrentFragment(profileFragment)
                }
                R.id.nav_conversion -> {
                    setCurrentFragment(conversionFragment)
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}