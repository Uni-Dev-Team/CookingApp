package com.unidevteam.cookingapp.activities.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.components.RecipeItemAdapter
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.services.DBManager
import kotlin.math.log

class HomeFragment : Fragment() {
    private lateinit var viewOfLayout : View
    private val recipesList : MutableList<CARecipe> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        viewOfLayout = inflater.inflate(R.layout.fragment_home, container, false)

        val titles : MutableList<String> = mutableListOf()

        var recipesAdapter = RecipeItemAdapter(requireActivity(), recipesList, titles)
        var recipeListView : ListView = viewOfLayout.findViewById(R.id.recipeItemsListView)
        recipeListView.adapter = recipesAdapter

        var searchText = viewOfLayout.findViewById<EditText>(R.id.searchEditText).text
        DBManager.getRecipesData(10)
            .addOnSuccessListener {
                if(searchText.isBlank()){
                    for(dSnap : DocumentSnapshot in it.documents) {
                        val recipe : CARecipe = CARecipe.fromData(dSnap.data!!)
                        titles.add(recipe.title)
                        recipesList.add(recipe)
                    }
                    if(it.documents.size > 0) {
                        recipesAdapter.notifyDataSetChanged()
                    }
                }

            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
            }

        viewOfLayout.findViewById<EditText>(R.id.searchEditText).addTextChangedListener{
            //TODO: Ricerca in base a searchText

        }
        return viewOfLayout
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}