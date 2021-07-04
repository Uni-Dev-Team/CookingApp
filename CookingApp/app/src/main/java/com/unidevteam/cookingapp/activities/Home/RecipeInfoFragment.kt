package com.unidevteam.cookingapp.activities.Home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CAIngredient
import com.unidevteam.cookingapp.models.CARecipe
import java.net.URL
import java.util.concurrent.Executors

class RecipeInfoFragment : Fragment() {
    private lateinit var viewOfLayout : View
    @SuppressLint("SetTextI18n", "CutPasteId", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOfLayout = inflater.inflate(R.layout.recipe_info_fragment, container, false)

        val numOfPersonValues : List<String> = listOf(
            "1 persona",
            "2 persone",
            "3 persone",
            "4 persone",
            "5 persone",
            "6 persone",
            "7 persone",
            "8 persone",
            "9 persone",
            "10 persone",
        )

        val numOfPersonAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, numOfPersonValues)
        viewOfLayout.findViewById<Spinner>(R.id.numberOfPersonSpinner).adapter = numOfPersonAdapter

        val ingredientsListViewItems : MutableList<String> = mutableListOf()
        val ingredientsListViewAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ingredientsListViewItems)
        viewOfLayout.findViewById<ListView>(R.id.ingredientsListView).adapter = ingredientsListViewAdapter

        viewOfLayout.findViewById<Button>(R.id.goBackButton).setOnClickListener {
            activity?.onBackPressed();
        }

        arguments?.getParcelable<CARecipe>("recipe")?.let {
            val rec : CARecipe = it
            Log.e(TAG, rec.title)

            // Image Download
            if (rec.imageURL == null) {
                viewOfLayout.findViewById<ImageView>(R.id.userProfilePicture).setImageResource(R.drawable.ic_baseline_account_circle_80)
            } else {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val url = URL(rec.imageURL.toString())
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    handler.post {
                        viewOfLayout.findViewById<ImageView>(R.id.recipeCoverImageView)
                            .setImageBitmap(bmp)
                    }
                }
            }
            viewOfLayout.findViewById<TextView>(R.id.recipeTitleInfoTextView).text = rec.title
            viewOfLayout.findViewById<TextView>(R.id.timeValueTextView).text = "${rec.time}m"
            viewOfLayout.findViewById<TextView>(R.id.difficultyValueTextView).text = rec.difficulty
            viewOfLayout.findViewById<TextView>(R.id.costValueTextView).text = rec.cost

            viewOfLayout.findViewById<TextView>(R.id.processTextValue).text = rec.process

            // TODO: popolare lo spinner e la list view
            viewOfLayout.findViewById<Spinner>(R.id.numberOfPersonSpinner).setSelection(rec.numOfPerson+1)

            for(ingredient : CAIngredient in rec.ingredients) {
                val value = "${ingredient.name} - ${ingredient.amount}${ingredient.unit}"
                ingredientsListViewItems.add(value)
            }
            ingredientsListViewAdapter.notifyDataSetChanged()

            // Lista della spesa
            viewOfLayout.findViewById<Button>(R.id.recipeActionMenuButton).setOnClickListener{ view ->
                /*var myClipboard = getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("simple text", rec.ingredientsString)

                myClipboard.setPrimaryClip(clip)*/

                val popup = PopupMenu(requireContext(), viewOfLayout.findViewById<Button>(R.id.recipeActionMenuButton))
                //Inflating the Popup using xml file
                popup.menuInflater.inflate(R.menu.menu_main, popup.menu)


                popup.setOnMenuItemClickListener({
                    if (it.itemId == R.id.shoppinglist) {
                        Toast.makeText(requireContext(), "One", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "None", Toast.LENGTH_SHORT).show()
                    }
                    true
                })

                popup.show()//showing popup men
            }
        }

        return viewOfLayout
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun newInstance(recipe: CARecipe) = RecipeInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("recipe", recipe)
            }
        }
        private val TAG = "Recipe Info"
    }
}