package com.unidevteam.cookingapp.activities.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R

class AddRecipeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_recipe, container, false)

    // override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    super.onViewCreated(view, savedInstanceState)
    // }
}