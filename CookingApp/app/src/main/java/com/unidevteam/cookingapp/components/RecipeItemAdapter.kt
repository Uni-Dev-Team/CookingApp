package com.unidevteam.cookingapp.components

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.models.CAUser
import com.unidevteam.cookingapp.services.DBManager
import java.net.URL
import java.util.concurrent.Executors

class RecipeItemAdapter(private val context : Activity, private val recipes : MutableList<CARecipe>, private val titles : List<String>)
    : ArrayAdapter<String>(context, R.layout.recipe_item, titles) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.recipe_item, null, true)

        // Get user info
        DBManager.getUserInfoFromUID(recipes[position].chefUID)
            .addOnSuccessListener {
                val user : CAUser = CAUser.fromData(it.data!!)
                // rowView.findViewById<ImageView>(R.id.userProfilePictureImageView).setImageBitmap()
                rowView.findViewById<TextView>(R.id.userDisplayNameTextView).text = "${user.name} ${user.surname}"

                rowView.findViewById<TextView>(R.id.recipeTitleTextView).text = recipes[position].title

                rowView.findViewById<TextView>(R.id.timeValueTextView).text = recipes[position].time
                rowView.findViewById<TextView>(R.id.difficultyValueTextView).text = recipes[position].difficulty
                rowView.findViewById<TextView>(R.id.costValueTextView).text = recipes[position].cost

                // var url = URL(user.imageURL)
                // Log.e(TAG, "URL: $url")
                // var bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                // rowView.findViewById<ImageView>(R.id.userProfilePictureImageView).setImageBitmap(bmp)

                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val url = URL(recipes[position].imageURL)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    handler.post {
                        rowView.findViewById<ImageView>(R.id.recipeImageView).setImageBitmap(bmp)
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Errore")
            }

        return rowView
    }

    companion object {
        private val TAG = "RecipeItemAdapter"
    }
}