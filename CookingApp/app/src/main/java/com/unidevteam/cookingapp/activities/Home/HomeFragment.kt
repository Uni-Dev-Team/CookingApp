package com.unidevteam.cookingapp.activities.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.services.DBManager

class HomeFragment : Fragment() {
    private lateinit var viewOfLayout : View
    val recipeScrollView : ScrollView = viewOfLayout.findViewById(R.id.homeRecipesScrollView)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_home, container, false)

        DBManager.getRecipesData(10)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
            }

        return viewOfLayout
    }

    private fun addRecipeItems(recipeItems: List<CARecipe>) {

    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}