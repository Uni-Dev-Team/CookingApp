package com.unidevteam.cookingapp

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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.*
import java.net.URL
import java.util.concurrent.Executors

class ProfileActivity : AppCompatActivity() {
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val storage = Firebase.storage
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<TextView>(R.id.lb_profileId).text = "Profile ID: ${user?.uid}"
        findViewById<TextView>(R.id.lb_displayName).text = "Display Name: ${user?.displayName}"
        findViewById<TextView>(R.id.lb_email).text = "Email: ${user?.email}"
        findViewById<TextView>(R.id.lb_isVerified).text = "Is Verified: ${user?.isEmailVerified}"
        findViewById<TextView>(R.id.lb_photoURL).text = "Photo URL: ${user?.photoUrl}"
        if (user != null) {
            if (user.photoUrl == null) {
                findViewById<ImageView>(R.id.img_profile).setImageResource(R.drawable.ic_baseline_account_circle_80)
            } else {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val url = URL(user.photoUrl.toString())
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    handler.post {
                        findViewById<ImageView>(R.id.img_profile).setImageBitmap(bmp)
                    }
                }
            }
        }

        findViewById<TextView>(R.id.btn_logout).setOnClickListener {
            Log.d(TAG, "Button Logout pressed")
            firebaseAuthSignOut()
            gotoLoginPage()
        }

        findViewById<ImageView>(R.id.img_profile).setOnClickListener {
            // Create an instance of the dialog fragment and show it

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this)
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Se seleziona l'ìmmagine dalla galleria
        if (requestCode == REQUEST_IMAGE_PATH && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d(TAG, "File selezionato: $selectedFile")
            if (selectedFile != null) {
                Log.d(TAG, "Path: $selectedFile")
                // TODO: 4/12/2021 Upload the image to FireStore (compressed!!)
                val imagePath : String? = getPathFromInputStreamUri(selectedFile)
                Log.d(TAG, "IMAGE PATH: $imagePath")

                val options : BitmapFactory.Options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap : Bitmap = BitmapFactory.decodeFile(imagePath, options)

                uploadProfileImage(bitmap)
            } else {
                Log.e(TAG, "Error: selected file was null")
            }
        }

        // Se fa la foto in tempo reale
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // TODO: 4/12/2021 Upload the image to FireStore (compressed!!)
            uploadProfileImage(imageBitmap)
        }
    }

    // Functions
    private fun getPathFromInputStreamUri(uri: Uri): String? {
        var filePath: String? = null
        uri.authority?.let {
            try {
                contentResolver.openInputStream(uri).use {
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

        val uploadTask = storageRef.child("profilePics/${user!!.uid}").putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(TAG, "Error: ${uploadTask.exception}")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d(TAG, "Success: ${taskSnapshot.metadata}")
            val downloadURLTask = storageRef.child("profilePics/${user.uid}").downloadUrl
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
                findViewById<ImageView>(R.id.img_profile).setImageBitmap(bmp)
            }
        }
    }
    // [END update_Image_view]

    // [START auth_sign_out]
    private fun firebaseAuthSignOut() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.delete()
        }
        auth.signOut()
        if (auth.currentUser == null) {
            Log.d(TAG, "User signed oud")
        } else {
            Log.d(TAG, "Error: User not signed oud")
        }
    }
    // [END auth_sign_out]

    // [START file_chooser]
   private fun fileChooser() {
        val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

        Log.d(TAG, "FILE CHOOSER CHIAMATO")
        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_IMAGE_PATH)
   }
    // [END file_chooser]
    private fun cameraShot() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    // [START goto_login_page]
    private fun gotoLoginPage(){
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    // [END goto_login_page]

    companion object {
        private const val TAG = "ProfileActivity"
        private const val REQUEST_IMAGE_PATH = 111
        private const val REQUEST_IMAGE_CAPTURE = 112

    }
}