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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unidevteam.cookingapp.R
import com.unidevteam.cookingapp.models.CAIngredient
import com.unidevteam.cookingapp.models.CARecipe
import com.unidevteam.cookingapp.services.DBManager
import com.unidevteam.cookingapp.util.RequestCodes
import java.io.*
import java.net.URL
import java.util.concurrent.Executors

class AddRecipeFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val storage = Firebase.storage
    private var imageLoaded : Boolean = false

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

        val difficultyItems = listOf<String>("Facile", "Normale", "Difficile")
        val costItems = listOf<String>("Basso", "Medio", "Alto")
        val unitItems = listOf<String>("g", "oz", "qt", "qb", "l", "cl", "ml")

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

        viewOfLayout.findViewById<Spinner>(R.id.recipeTimeSpinner).adapter = timeListViewAdapter
        viewOfLayout.findViewById<Spinner>(R.id.recipeDifficultySpinner).adapter = difficultyListViewAdapter
        viewOfLayout.findViewById<Spinner>(R.id.recipeCostSpinner).adapter = costListViewAdapter

        viewOfLayout.findViewById<Spinner>(R.id.recipeNewIngredientUnitSpinner).adapter = amountUnitListViewAdapter
        viewOfLayout.findViewById<ListView>(R.id.recipeIngredientsListView).adapter = ingredientsListViewAdapter

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
                ingredientsItems.add("${ingredientNameEditText.text} - ${ingredientAmountEditText.text} $ingredientUnit")
                ingredientsListViewAdapter.notifyDataSetChanged()

                viewOfLayout.findViewById<EditText>(R.id.recipeNewIngredientValue).text.clear()
                viewOfLayout.findViewById<EditText>(R.id.recipeNewIngredientAmount).text.clear()
            } else {
                Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show()
            }
        }

        viewOfLayout.findViewById<Button>(R.id.recipeAddRecipe).setOnClickListener {
            val coverImageView : ImageView = viewOfLayout.findViewById(R.id.imageViewRecepie)
            val recipeNameEditText : EditText = viewOfLayout.findViewById(R.id.recepieTitle)
            val timeSpinner : Spinner = viewOfLayout.findViewById(R.id.recipeTimeSpinner)
            val difficultySpinner : Spinner = viewOfLayout.findViewById(R.id.recipeDifficultySpinner)
            val costSpinner : Spinner = viewOfLayout.findViewById(R.id.recipeCostSpinner)
            val ingredientsListView : ListView = viewOfLayout.findViewById(R.id.recipeIngredientsListView)
            val processEditText : EditText = viewOfLayout.findViewById(R.id.addRecipeProcess)

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
                        val timeValue : String = timeSpinner.selectedItem.toString()
                        val difficultyValue : String = difficultySpinner.selectedItem.toString()
                        val costValue : String = costSpinner.selectedItem.toString()
                        val recipeProcess : String = processEditText.text.toString()
                        val imageData : Bitmap = coverImageView.drawable.toBitmap()
                        val ingredients : MutableList<CAIngredient> = mutableListOf()

                        for(i : Int in 0 until ingredientsItems.size) {
                            val ingredientItems : List<String> = ingredientsItems[i].split('-')
                            val ingredientName : String = ingredientItems[0].replace("\\s".toRegex(), "")
                            val amountData : List<String> = ingredientItems[1].trim().split(' ')
                            val ingredientAmount : String = amountData[0]
                            val ingredientUnit : String = amountData[1]

                            val ingredient : CAIngredient = CAIngredient(ingredientName, ingredientAmount, ingredientUnit)

                            ingredients.add(ingredient)
                        }

                        val recipe : CARecipe = CARecipe(recipeName, ingredients, timeValue, difficultyValue, costValue, recipeProcess, 0, FirebaseAuth.getInstance().currentUser!!.uid)

                        DBManager.addNewRecipe(recipe)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Ricetta aggiunta con successo!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Si è verificato un errore, riprova", Toast.LENGTH_SHORT).show()
                            }

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

                // TODO Upload dell'img solo una volta si decide di caricare la ricetta
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
    private fun uploadProfileImage(imageData: Bitmap) {
        lateinit var bitmapImage : Bitmap

        // Taglia l'immagine per ottenerne una quadrata
        if(imageData.width != imageData.height) {
            val shortestSide = Integer.min(imageData.width, imageData.height)

            val xOffset = (imageData.width - shortestSide) / 2
            val yOffset = (imageData.height - shortestSide) / 2

            bitmapImage =
                Bitmap.createBitmap(imageData, xOffset, yOffset, shortestSide, shortestSide)
        } else {
            bitmapImage = imageData
        }

        val storageRef = storage.reference

        val baos = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.child("recipePics/${user!!.uid}/${viewOfLayout.findViewById<TextView>(R.id.recepieTitle).text}").putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(TAG, "Error: ${uploadTask.exception}")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d(TAG, "Success: ${taskSnapshot.metadata}")

            // TODO: Cambiare reference per il caricamento dell' img della ricetta
            val downloadURLTask = storageRef.child("recipePics/${user!!.uid}/${viewOfLayout.findViewById<TextView>(R.id.recepieTitle).text}").downloadUrl
            downloadURLTask.addOnSuccessListener { downloadURL ->
                // Aggiorna il campo dell'URL della foto profilo
                val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(downloadURL).build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Log.d(TAG, "Log: Photo URL set successively set")
                            updateImageView()
                        } else {
                            Log.d(TAG, "Error: Photo URL set successively failed")
                        }
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
        private const val TAG = "AddRecipeFragment"
    }
}