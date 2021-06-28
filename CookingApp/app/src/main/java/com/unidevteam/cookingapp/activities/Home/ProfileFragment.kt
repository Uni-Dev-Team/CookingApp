package com.unidevteam.cookingapp.activities.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.unidevteam.cookingapp.LoginActivity
import com.unidevteam.cookingapp.R

class ProfileFragment : Fragment() {
    private lateinit var viewOfLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_profile, container, false)

        viewOfLayout.findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            Log.d(TAG, "Button Logout pressed")
            firebaseAuthSignOut()
            gotoLoginPage()
        }

        return viewOfLayout
    }

    // override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //     super.onViewCreated(view, savedInstanceState)
    // }

    // [START goto_login_page]
    private fun gotoLoginPage(){
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    // [END goto_login_page]

    // [START auth_sign_out]
    private fun firebaseAuthSignOut() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.delete()
        }
        auth.signOut()
        if (auth.currentUser == null) {
            Log.d(TAG, "User signed oud")
        } else {
            Log.d(TAG, "Error: User not signed oud")
        }
    }
    // [END auth_sign_out]

    companion object {
        private const val TAG = "ProfileActivity"
        private const val REQUEST_IMAGE_PATH = 111
        private const val REQUEST_IMAGE_CAPTURE = 112
    }
}