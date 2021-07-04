package com.unidevteam.cookingapp.activities.Home

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.unidevteam.cookingapp.R
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



            // Lista della spesa
            viewOfLayout.findViewById<Button>(R.id.recipeActionMenuButton).setOnClickListener{ view ->
                var myClipboard = getSystemService(context!!, ClipboardManager::class.java) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("simple text", rec.ingredientsString)

                myClipboard.setPrimaryClip(clip)
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