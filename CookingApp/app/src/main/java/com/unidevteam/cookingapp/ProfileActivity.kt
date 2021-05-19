package com.unidevteam.cookingapp

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.net.URL
import java.util.concurrent.Executors


class ProfileActivity : AppCompatActivity() {
    private val user: FirebaseUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<TextView>(R.id.lb_profileId).text = "Profile ID: ${user.uid}"
        findViewById<TextView>(R.id.lb_displayName).text = "Display Name: ${user.displayName}"
        findViewById<TextView>(R.id.lb_email).text = "Email: ${user.email}"
        findViewById<TextView>(R.id.lb_isVerified).text = "Is Verified: ${user.isEmailVerified}"
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

        if (requestCode == REQUEST_IMAGE_PATH && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d(TAG, "Path: $selectedFile")
            // TODO: 4/12/2021 Upload the image to FireStore (compressed!!)
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            Log.d(TAG, "Info: $imageBitmap")
            // TODO: 4/12/2021 Upload the image to FireStore (compressed!!)
        }
    }

    // Functions
    // [START auth_sign_out]
    private fun firebaseAuthSignOut() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        if (auth.currentUser.isAnonymous) { auth.currentUser.delete() }
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

        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_IMAGE_PATH)
   }
    // [END file_chooser]
    private fun cameraShot() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)

        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    // [START goto_login_page]
    private fun gotoLoginPage(){
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]

    companion object {
        private const val TAG = "ProfileActivity"
        private const val REQUEST_IMAGE_PATH = 111
        private const val REQUEST_IMAGE_CAPTURE = 112

    }
}