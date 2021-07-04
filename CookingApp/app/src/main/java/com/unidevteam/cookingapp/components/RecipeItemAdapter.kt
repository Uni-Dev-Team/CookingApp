package com.unidevteam.cookingapp.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.models.CAUser
import com.unidevteam.cookingapp.services.DBManager
import java.net.URL
import java.util.concurrent.Executors

class RecipeItemAdapter(context: Context, private val recipes: MutableList<CARecipe>)
    : ArrayAdapter<CARecipe>(context, R.layout.recipe_item, recipes), Filterable {
    private var allRecipes : List<CARecipe>? = recipes

    override fun getCount(): Int {
        return if (allRecipes == null) 0 else allRecipes!!.size
    }

    override fun getItem(p0: Int): CARecipe? {
        return if (allRecipes == null) null else allRecipes!!.get(p0)
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.recipe_item, parent, false)

        rowView.findViewById<TextView>(R.id.recipeTitleTextView).text = allRecipes!![position].title

        rowView.findViewById<TextView>(R.id.timeValueTextView).text = "${allRecipes!![position].time}m"
        rowView.findViewById<TextView>(R.id.difficultyValueTextView).text = allRecipes!![position].difficulty
        rowView.findViewById<TextView>(R.id.costValueTextView).text = allRecipes!![position].cost

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val url = URL(allRecipes!![position].imageURL)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            handler.post {
                rowView.findViewById<ImageView>(R.id.recipeImageView).setImageBitmap(bmp)
            }
        }

        // Get user info
        DBManager.getUserInfoFromUID(allRecipes!![position].chefUID)
            .addOnSuccessListener {
                val user : CAUser = CAUser.fromData(it.data!!)
                rowView.findViewById<TextView>(R.id.userDisplayNameTextView).text = "${user.name} ${user.surname}"

                /*val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val url = URL(user.imageURL)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    handler.post {
                        rowView.findViewById<ImageView>(R.id.userProfilePictureImageView).setImageBitmap(bmp)
                    }
                }*/
            }
            .addOnFailureListener {
                Log.e(TAG, "Errore")
            }
        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                allRecipes = filterResults.values as List<CARecipe>?
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = FilterResults()

                filterResults.values = if (queryString==null || queryString.isEmpty())
                    recipes
                else
                    recipes.filter {
                        it.title.toLowerCase().contains(queryString) ||
                        it.time.toLowerCase().contains(queryString) ||
                        it.difficulty.toLowerCase().contains(queryString) ||
                        it.cost.toLowerCase().contains(queryString) ||
                        it.ingredientsString.toLowerCase().contains(queryString) ||
                        it.typePortata.toLowerCase().contains(queryString) ||
                        it.typology.toLowerCase().contains(queryString)
                    }

                return filterResults
            }
        }
    }

    companion object {
        private val TAG = "RecipeItemAdapter"
    }
}