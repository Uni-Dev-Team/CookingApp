package com.unidevteam.cookingapp.activities.Home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.components.RecipeItemAdapter
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.services.DBManager

class HomeFragment : Fragment() {
    private lateinit var viewOfLayout : View

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        viewOfLayout = inflater.inflate(R.layout.fragment_home, container, false)

            val recipesList : MutableList<CARecipe> = mutableListOf()
            val recipesAdapter = RecipeItemAdapter(requireContext(), recipesList)
            val recipeListView: ListView = viewOfLayout.findViewById(R.id.recipeItemsListView)
            recipeListView.adapter = recipesAdapter

            recipeListView.setOnItemClickListener { parent, view, position, id ->
                val element = recipesAdapter.getItem(position)

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fl_wrapper, RecipeInfoFragment.newInstance(element!!))
                transaction.disallowAddToBackStack()
                transaction.commit()
            }

            val searchText = viewOfLayout.findViewById<EditText>(R.id.searchEditText).text
            DBManager.getRecipesData(10)
                .addOnSuccessListener {
                    if (searchText.isBlank()) {
                        for (dSnap: DocumentSnapshot in it.documents) {
                            val recipe: CARecipe = CARecipe.fromData(dSnap.data!!)
                            recipesList.add(recipe)
                        }
                        if (it.documents.size > 0) {
                            recipesAdapter.notifyDataSetChanged()
                        }
                    }

                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }

            viewOfLayout.findViewById<Button>(R.id.searchButton).setOnClickListener {
                val searchValue : String = viewOfLayout.findViewById<EditText>(R.id.searchEditText).text.toString()

                if(searchValue.isNotEmpty()) {
                   recipesAdapter.filter.filter(searchValue)
                }
            }

        return viewOfLayout
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}