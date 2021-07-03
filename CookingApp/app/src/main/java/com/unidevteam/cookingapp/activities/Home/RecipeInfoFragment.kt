package com.unidevteam.cookingapp.activities.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CARecipe

class RecipeInfoFragment : Fragment() {
    private lateinit var viewOfLayout : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.recipe_info_fragment, container, false)

        Log.e(TAG, "EEEEEEEEEEEEEEEEEEE")

        arguments?.getParcelable<CARecipe>("recipe")?.let {
            val rec : CARecipe = it
            Log.e(TAG, rec.title)
        }

        return viewOfLayout
    }

    companion object {
        fun newInstance(recipe: CARecipe) = RecipeInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("recipe", recipe)
            }
        }

        private val TAG = "Recipe Info"
    }
}