package com.unidevteam.cookingapp.activities.Home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CAIngredient
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.services.DBManager
import com.unidevteam.cookingapp.util.RequestCodes
import java.io.*
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.Executors
import java.util.regex.Pattern


class AddRecipeFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val storage = Firebase.storage
    private var imageLoaded : Boolean = false
    private  var documentID: String? = null

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewOfLayout = inflater.inflate(R.layout.fragment_add_recipe, container, false)


        val numOfPersonItems : MutableList<String> = mutableListOf("1 persona", "2 persone", "3 persone", "4 persone", "5 persone")



        val timeItems : MutableList<String> = mutableListOf<String>()
        (5..60 step 5).forEach {
            timeItems.add(it.toString())
        }

        val difficultyItems = listOf<String>("Facile", "Normale", "Difficile")
        val costItems = listOf<String>("Basso", "Medio", "Alto")
        val unitItems = listOf<String>("g", "qt", "ml", "cl", "l", "cup", "tbs")

        val typePortataItems = listOf("Generale", "Antipasto", "Primo", "Secondo", "Dolce")
        val typologyItems = listOf("Tutto", "Carne", "Pesce", "Vegetariano", "Vegano")

        val numOfPersonAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            numOfPersonItems
        )

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

        val typePortataAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            typePortataItems
        )

        val typologyAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            typologyItems
        )

        val amountUnitListViewAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            unitItems
        )

        val ingredientsItems = mutableListOf<String>()
        val ingredientsListViewAdapter : ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ingredientsItems
        )

        viewOfLayout.findViewById<Spinner>(R.id.numOfPeopleSpinner).adapter = numOfPersonAdapter

        viewOfLayout.findViewById<Spinner>(R.id.recipeTimeSpinner).adapter = timeListViewAdapter
        viewOfLayout.findViewById<Spinner>(R.id.recipeDifficultySpinner).adapter = difficultyListViewAdapter
        viewOfLayout.findViewById<Spinner>(R.id.recipeCostSpinner).adapter = costListViewAdapter

        viewOfLayout.findViewById<Spinner>(R.id.recipeNewIngredientUnitSpinner).adapter = amountUnitListViewAdapter
        viewOfLayout.findViewById<ListView>(R.id.recipeIngredientsListView).adapter = ingredientsListViewAdapter

        viewOfLayout.findViewById<Spinner>(R.id.typePortataSpinner).adapter = typePortataAdapter
        viewOfLayout.findViewById<Spinner>(R.id.typologySpinner).adapter = typologyAdapter

        viewOfLayout.findViewById<ImageView>(R.id.imageViewRecepie).setOnClickListener {
            // Create an instance of the dialog fragment and show it

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Choose source: ")
                .setPositiveButton("Camera"
                ) { _, _ ->
                    cameraShot()
                }
                .setNegativeButton("File"
                ) { _, _ ->
                    fileChooser()
                }
            // AlertDialog Builder class
            val dialog: AlertDialog.Builder = builder
            dialog.show()
        }

        // DA REIMPLEMENTARE CON UNA LIST VIEW
        /*viewOfLayout.findViewById<Button>(R.id.btn_AddFields).setOnClickListener() {
            // creating TextView programmatically
            val newTextView = TextView(activity)
            newTextView.text = "TextView"
            viewOfLayout.findViewById<LinearLayout>(R.id.ingredientsLayout).addView(newTextView)
        }*/

        viewOfLayout.findViewById<Button>(R.id.recipeAddIngredientButton).setOnClickListener {
            val ingredientNameEditText = viewOfLayout.findViewById<EditText>(R.id.recipeNewIngredientValue)
            val ingredientAmountEditText = viewOfLayout.findViewById<EditText>(R.id.recipeNewIngredientAmount)

            val ingredientUnit = viewOfLayout.findViewById<Spinner>(R.id.recipeNewIngredientUnitSpinner).selectedItem?.toString()

            if(ingredientNameEditText.text.isNotEmpty() && ingredientAmountEditText.text.isNotEmpty()) {
                // Controlli formato input
                if(Pattern.compile("[a-zA-Z ]*").matcher(ingredientNameEditText.text).matches()) {
                    val amount : String = ingredientAmountEditText.text.toString().trim()
                    if(Pattern.compile("[0-9]*").matcher(amount).matches()) {
                        ingredientsItems.add("${ingredientNameEditText.text.trim()} - $amount $ingredientUnit")

                        ingredientsListViewAdapter.notifyDataSetChanged()

                        viewOfLayout.findViewById<EditText>(R.id.recipeNewIngredientValue).text.clear()
                        viewOfLayout.findViewById<EditText>(R.id.recipeNewIngredientAmount).text.clear()

                        viewOfLayout.findViewById<Spinner>(R.id.recipeNewIngredientUnitSpinner).setSelection(0)
                    } else {
                        Toast.makeText(requireContext(), "Inserisci un valore numerico", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Caratteri speciali non ammessi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show()
            }
        }

        viewOfLayout.findViewById<ListView>(R.id.recipeIngredientsListView).onItemLongClickListener =
            OnItemLongClickListener { arg0, arg1, pos, id ->
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Vuoi cancellare ${ingredientsItems[id.toInt()].toString()} ?")
                    .setPositiveButton("Sì"
                    ) { _, _ ->
                        ingredientsItems.removeAt(id.toInt())
                        ingredientsListViewAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("No"
                    ) { _, _ ->

                    }
                // AlertDialog Builder class
                val dialog: AlertDialog.Builder = builder
                dialog.show()
                true
            }
        viewOfLayout.findViewById<Button>(R.id.recipeAddRecipe).setOnClickListener {
            val coverImageView : ImageView = viewOfLayout.findViewById(R.id.imageViewRecepie)
            val recipeNameEditText : EditText = viewOfLayout.findViewById(R.id.recipeTitle)
            val numOfPersonSpinner : Spinner = viewOfLayout.findViewById(R.id.numOfPeopleSpinner)
            val timeSpinner : Spinner = viewOfLayout.findViewById(R.id.recipeTimeSpinner)
            val difficultySpinner : Spinner = viewOfLayout.findViewById(R.id.recipeDifficultySpinner)
            val costSpinner : Spinner = viewOfLayout.findViewById(R.id.recipeCostSpinner)
            val ingredientsListView : ListView = viewOfLayout.findViewById(R.id.recipeIngredientsListView)
            val processEditText : EditText = viewOfLayout.findViewById(R.id.addRecipeProcess)
            val typePortataSpinner : Spinner = viewOfLayout.findViewById(R.id.typePortataSpinner)
            val typologySpinner : Spinner = viewOfLayout.findViewById(R.id.typologySpinner)

            // Empty fields check
            if(recipeNameEditText.text.isNotEmpty() && processEditText.text.isNotEmpty()) {
                Log.e(TAG, "NOT EMPTY FIELDS CHECK PASSED")
                // Image loaded check
                if(imageLoaded) {
                    Log.e(TAG, "IMAGE LOADED CHECK PASSED")
                    // Ingredients check
                    if(!ingredientsListViewAdapter.isEmpty) {
                        Log.e(TAG, "INGREDIENTS CHECK PASSED")

                        // Create Recipe object and load it to Firestore
                        val recipeName : String = recipeNameEditText.text.toString()
                        val numOfPersonValue : Int = Integer.parseInt(numOfPersonSpinner.selectedItem.toString().split(" ")[0].trim())
                        val timeValue : String = timeSpinner.selectedItem.toString()
                        val difficultyValue : String = difficultySpinner.selectedItem.toString()
                        val costValue : String = costSpinner.selectedItem.toString()
                        val recipeProcess : String = processEditText.text.toString()
                        val imageData : Bitmap = coverImageView.drawable.toBitmap()
                        val ingredients : MutableList<CAIngredient> = mutableListOf()
                        val typePortataValue : String = typePortataSpinner.selectedItem.toString()
                        val typologyValue : String = typologySpinner.selectedItem.toString()

                        for(i : Int in 0 until ingredientsItems.size) {
                            val ingredientItems : List<String> = ingredientsItems[i].split('-')
                            val ingredientName : String = ingredientItems[0].trim()
                            val amountData : List<String> = ingredientItems[1].trim().split(' ')
                            val ingredientAmount : String = amountData[0]
                            val ingredientUnit : String = amountData[1]

                            val ingredient = CAIngredient(ingredientName, ingredientAmount, ingredientUnit)

                            ingredients.add(ingredient)
                        }
                        val docID = if (documentID == null) DBManager.genRecipeDocumentID() else documentID

                        val recipe = CARecipe(
                            docID!!,
                            null,
                            recipeName,
                            ingredients,
                            timeValue,
                            difficultyValue,
                            costValue,
                            recipeProcess,
                            numOfPersonValue,
                            0,
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            typePortataValue,
                            typologyValue
                        )
                        uploadProfileImage(imageData, recipe)

                        val bitmap : Bitmap? = BitmapFactory.decodeResource(requireContext().resources, R.mipmap.ic_recipe_cover_placeholder)
                        if(bitmap != null) coverImageView.setImageBitmap(bitmap)

                        recipeNameEditText.text.clear()
                        processEditText.text.clear()

                        ingredientsItems.clear()
                        ingredientsListViewAdapter.notifyDataSetChanged()

                        numOfPersonSpinner.setSelection(0)
                        timeSpinner.setSelection(0)
                        difficultySpinner.setSelection(0)
                        costSpinner.setSelection(0)
                        typePortataSpinner.setSelection(0)
                        typologySpinner.setSelection(0)
                    } else {
                        // No ingredients added warning
                        Toast.makeText(requireContext(), "Nessun ingrediente presente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Image not loaded warning
                    Toast.makeText(requireContext(), "Devi caricare una foto per la ricetta", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Empty fields warning
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show()
            }
        }

        arguments?.getParcelable<CARecipe>("recipe")?.let {
            if (!it.equals(null)){
                val rec: CARecipe = it
                documentID = rec.recipeID
                Log.e(TAG, rec.title)

                // Sostituzione

                // Image Download
                if (rec.imageURL == null) {
                    viewOfLayout.findViewById<ImageView>(R.id.imageViewRecepie).setImageResource(R.drawable.ic_baseline_account_circle_80)
                } else {
                    val executor = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    executor.execute {
                        val url = URL(rec.imageURL.toString())
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        imageLoaded = true
                        handler.post {
                            viewOfLayout.findViewById<ImageView>(R.id.imageViewRecepie)
                                .setImageBitmap(bmp)
                        }
                    }
                }

                // EditText Filling

                viewOfLayout.findViewById<EditText>(R.id.recipeTitle).setText(rec.title)
                viewOfLayout.findViewById<Spinner>(R.id.recipeTimeSpinner).setSelection(timeItems.indexOf(rec.time))
                viewOfLayout.findViewById<Spinner>(R.id.recipeDifficultySpinner).setSelection(difficultyItems.indexOf(rec.difficulty))
                viewOfLayout.findViewById<Spinner>(R.id.recipeCostSpinner).setSelection(costItems.indexOf(rec.cost))
                viewOfLayout.findViewById<Spinner>(R.id.typePortataSpinner).setSelection(typePortataItems.indexOf(rec.typePortata))
                viewOfLayout.findViewById<Spinner>(R.id.typologySpinner).setSelection(typologyItems.indexOf(rec.typology))
                viewOfLayout.findViewById<Spinner>(R.id.numOfPeopleSpinner).setSelection(rec.numOfPerson-1)
                viewOfLayout.findViewById<EditText>(R.id.addRecipeProcess).setText(rec.process)
                for (ingredient in rec.ingredients) {
                    val string: String = "${ingredient.name} - ${ingredient.amount} ${ingredient.unit}"
                    ingredientsItems.add(string)
                }
                ingredientsListViewAdapter.notifyDataSetChanged()
            }
        }

        return viewOfLayout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Se seleziona l'ìmmagine dalla galleria
        if (requestCode == RequestCodes.REQUEST_IMAGE_PATH && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d(TAG, "File selezionato: $selectedFile")
            if (selectedFile != null) {
                Log.d(TAG, "Path: $selectedFile")

                val imagePath : String? = getPathFromInputStreamUri(selectedFile)
                Log.d(TAG, "IMAGE PATH: $imagePath")

                val options : BitmapFactory.Options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap : Bitmap = BitmapFactory.decodeFile(imagePath, options)

                viewOfLayout.findViewById<ImageView>(R.id.imageViewRecepie).setImageBitmap(bitmap)

                imageLoaded = true
            } else {
                Log.e(TAG, "Error: selected file was null")
            }
        }

        // Se fa la foto in tempo reale
        if (requestCode == RequestCodes.REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            viewOfLayout.findViewById<ImageView>(R.id.imageViewRecepie).setImageBitmap(bitmap)
        }
    }

    // Functions
    private fun getPathFromInputStreamUri(uri: Uri): String? {
        var filePath: String? = null
        uri.authority?.let {
            try {
                requireActivity().contentResolver.openInputStream(uri).use {
                    val photoFile: File? = createTemporalFileFrom(it)
                    filePath = photoFile?.path
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return filePath
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null
        return if (inputStream == null) targetFile
        else {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            targetFile = File.createTempFile("tempPicture", "jpg")
            FileOutputStream(targetFile).use { out ->
                while (inputStream.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.flush()
            }
            targetFile
        }
    }

    // [START upload_Image_to_Firestore]
    @SuppressLint("CutPasteId")
    private fun uploadProfileImage(imageData: Bitmap, recipe: CARecipe) {
        lateinit var bitmapImage : Bitmap

        // Taglia l'immagine per ottenerne una quadrata
        if(imageData.width != imageData.height) {
            val shortestSide = Integer.min(imageData.width, imageData.height)

            val xOffset = (imageData.width - shortestSide) / 2
            val yOffset = (imageData.height - shortestSide) / 2

            bitmapImage = Bitmap.createBitmap(imageData, xOffset, yOffset, shortestSide, shortestSide)
        } else {
            bitmapImage = imageData
        }

        val storageRef = storage.reference

        val baos = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val stringToHash : String = recipe.title + recipe.time + recipe.difficulty + recipe.cost + recipe.process
        val messageDigest = MessageDigest.getInstance("SHA-1").digest(stringToHash.toByteArray(Charsets.UTF_8))
        val hashedString = StringBuilder()
        for(b: Byte in messageDigest) {
            hashedString.append(String.format("%02X", b))
        }
        val imageName : String = hashedString.toString()

        val uploadTask = storageRef.child("recipePics/${user!!.uid}/$imageName").putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(TAG, "Error: ${uploadTask.exception}")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d(TAG, "Success: ${taskSnapshot.metadata}")

            val downloadURLTask = storageRef.child("recipePics/${user.uid}/$imageName").downloadUrl
            downloadURLTask.addOnSuccessListener { downloadURL ->
                recipe.imageURL = downloadURL.toString()

                DBManager.addNewRecipe(recipe)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Ricetta aggiunta con successo!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Si è verificato un errore, riprova", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    // [END upload_Image_to_Firestore]

    // [START update_Image_view]
    private fun updateImageView() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val url = URL(user!!.photoUrl.toString())
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            handler.post {
                viewOfLayout.findViewById<ImageView>(R.id.imageViewRecepie).setImageBitmap(bmp)
            }
        }
    }
    // [END update_Image_view]

    // [START file_chooser]
    private fun fileChooser() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        Log.d(AddRecipeFragment.TAG, "FILE CHOOSER CHIAMATO")
        startActivityForResult(
            Intent.createChooser(intent, "Select a file"),
            RequestCodes.REQUEST_IMAGE_PATH
        )
    }
    // [END file_chooser]

    // [START camera_shot]
    private fun cameraShot() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(intent, RequestCodes.REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }
    // [END camera_shot]

    companion object {
        fun newInstance(recipe: CARecipe) = AddRecipeFragment().apply {
            arguments = Bundle().apply {
                putParcelable("recipe", recipe)
            }
        }
        private const val TAG = "AddRecipeFragment"
    }
}