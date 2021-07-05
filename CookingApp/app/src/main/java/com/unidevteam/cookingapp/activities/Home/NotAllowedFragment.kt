package com.unidevteam.cookingapp.activities.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.unidevteam.cookingapp.LoginActivity
import com.unidevteam.cookingapp.R

class NotAllowedFragment : Fragment() {
    private lateinit var viewOfLayout : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewOfLayout = inflater.inflate(R.layout.fragment_not_allowed, container, false)

        viewOfLayout.findViewById<Button>(R.id.loginButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return viewOfLayout
    }
}