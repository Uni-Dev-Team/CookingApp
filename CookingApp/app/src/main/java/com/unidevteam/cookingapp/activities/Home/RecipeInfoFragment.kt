package com.unidevteam.cookingapp.activities.Home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CAIngredient
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.services.DBManager
import java.net.URL
import java.util.concurrent.Executors

class RecipeInfoFragment : Fragment() {
    private lateinit var viewOfLayout : View
    private lateinit var recipe : CARecipe

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
            requireActivity().onBackPressed();
        }

        arguments?.getParcelable<CARecipe>("recipe")?.let {
            val rec : CARecipe = it
            recipe = rec
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
            viewOfLayout.findViewById<Spinner>(R.id.numberOfPersonSpinner).setSelection(rec.numOfPerson-1)

            for(ingredient : CAIngredient in rec.ingredients) {
                val value = "${ingredient.name} - ${ingredient.amount}${ingredient.unit}"
                ingredientsListViewItems.add(value)
            }
            ingredientsListViewAdapter.notifyDataSetChanged()
        }

        viewOfLayout.findViewById<Spinner>(R.id.numberOfPersonSpinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newNumOfPerson = position + 1
                ingredientsListViewItems.clear()
                for(ingredient : CAIngredient in recipe.ingredients) {
                    val newAmount : Double = ((ingredient.amount).toDouble() / recipe.numOfPerson) * newNumOfPerson
                    val value = "${ingredient.name} - ${newAmount}${ingredient.unit}"
                    ingredientsListViewItems.add(value)
                }
                ingredientsListViewAdapter.notifyDataSetChanged()
            }
        }

        // Lista della spesa
        viewOfLayout.findViewById<Button>(R.id.recipeActionMenuButton).setOnClickListener{ view ->
            val popup = PopupMenu(requireContext(), viewOfLayout.findViewById<Button>(R.id.recipeActionMenuButton))
            //Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)

            popup.setOnMenuItemClickListener{ it ->
                when (it.itemId) {
                    R.id.edit -> {
                        if(recipe.chefUID != FirebaseAuth.getInstance().currentUser!!.uid) {
                            Toast.makeText(requireContext(), "Non sei il proprietario di questa ricetta!", Toast.LENGTH_SHORT).show()
                        } else {
                            val transaction =
                                requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(
                                R.id.fl_wrapper,
                                AddRecipeFragment.newInstance(recipe!!)
                            )
                            transaction.disallowAddToBackStack()
                            transaction.commit()
                        }
                    }
                    R.id.shoppinglist -> {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"

                        val numOfPersonSpinner = viewOfLayout.findViewById<Spinner>(R.id.numberOfPersonSpinner)
                        val ings : MutableList<CAIngredient> = mutableListOf()

                        for(ingredient : CAIngredient in recipe.ingredients) {
                            val newAmount : Double = ((ingredient.amount).toDouble() / recipe.numOfPerson) * (numOfPersonSpinner.selectedItemPosition+1)
                            ings.add(CAIngredient(ingredient.name, newAmount.toString(), ingredient.unit))
                        }
                        val sub : String = CARecipe.toGroceryList(ings)

                        intent.putExtra(Intent.EXTRA_TEXT, sub)
                        startActivity(Intent.createChooser(intent, "Condividi con"))
                    }
                    R.id.remove -> {
                        if(recipe.chefUID != FirebaseAuth.getInstance().currentUser!!.uid) {
                            Toast.makeText(requireContext(), "Non sei il proprietario di questa ricetta!", Toast.LENGTH_SHORT).show()
                        } else {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Sei sicuro di voler eliminare questa ricetta?")
                                .setNegativeButton(
                                    "Annulla"
                                ) { _, _ -> }
                                .setPositiveButton(
                                    "Elimina"
                                ) { _, _ ->
                                    DBManager.getRecipe(recipe)
                                        .addOnSuccessListener {
                                            if (it.documents.size > 0) {
                                                val docId: String = it.documents.first().id

                                                Log.e(TAG, "ID DOCUMENTO: $docId")

                                                DBManager.removeRecipe(docId)
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Ricetta rimossa",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                requireActivity().onBackPressed()
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Errore imprevisto",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            // AlertDialog Builder class
                            val dialog: AlertDialog.Builder = builder
                            dialog.show()
                        }
                    }
                }
                true
            }

            popup.show()//showing popup men
        }

        return viewOfLayout
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Menù azioni")
        menu.add(0, v.id, 0, "Modifica")
        menu.add(0, v.id, 1, "Lista della spesa")
        menu.add(1, v.id, 0, "Rimuovi")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when {
            item.title == "Modifica" -> {
                Toast.makeText(requireContext(), "Modifica", Toast.LENGTH_LONG).show()
                return true
            }
            item.title == "Lista della spesa" -> {
                Toast.makeText(requireContext(), "Lista della spesa", Toast.LENGTH_LONG).show()
                return true
            }
            item.title == "Rimuovi" -> {
                Toast.makeText(requireContext(), "Rimuovi", Toast.LENGTH_LONG).show()
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                Log.d("API123", "done")
                return true
            }
            R.id.shoppinglist -> {
                Log.d("API123", "done")
                return true
            }
            R.id.remove -> {
                Log.d("API123", "done")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
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