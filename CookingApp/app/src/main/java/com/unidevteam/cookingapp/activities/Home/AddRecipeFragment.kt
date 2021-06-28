package com.unidevteam.cookingapp.activities.Home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R

class AddRecipeFragment : Fragment() {
    private lateinit var viewOfLayout: View

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        var timeItems : MutableList<String> = mutableListOf<String>()
        (5..60 step 5).forEach {
            timeItems.add(it.toString())
        }

        val difficultyItems = mutableListOf<String>("Facile", "Normale", "Difficile")
        val costItems = mutableListOf<String>("Basso", "Medio", "Alto")

        val timeListViewAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            timeItems
        )

        val difficultyListViewAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            difficultyItems
        )

        val costListViewAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            costItems
        )

        viewOfLayout.findViewById<Spinner>(R.id.recipeTimeSpinner).adapter = timeListViewAdapter
        viewOfLayout.findViewById<Spinner>(R.id.recipeDifficultySpinner).adapter = difficultyListViewAdapter
        viewOfLayout.findViewById<Spinner>(R.id.recipeCostSpinner).adapter = costListViewAdapter

        return viewOfLayout
    }

    // override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    super.onViewCreated(view, savedInstanceState)
    // }

    companion object {
        private const val TAG = "AddRecipeFragment"
    }
}