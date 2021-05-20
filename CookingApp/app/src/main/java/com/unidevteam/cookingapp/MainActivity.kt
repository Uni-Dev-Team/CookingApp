package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [START config_signing]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signing]

        // Configure Facebook Sign In
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        // Set permission
        findViewById<LoginButton>(R.id.btn_facebookSignIn).setPermissions("email", "public_profile")


        // Firebase
        auth = FirebaseAuth.getInstance()

        // Buttons Listener

        findViewById<TextView>(R.id.lb_loginPage).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        findViewById<TextView>(R.id.lb_guest).setOnClickListener {
            d(TAG, "Label guest pressed")
            firebaseAuthAnonymously()
        }

        findViewById<TextView>(R.id.lb_passwordForgotten).setOnClickListener {
            d(TAG, "Password reset pressed")
            firebasePasswordReset(findViewById<TextView>(R.id.lb_email).text.toString())
        }

        findViewById<SignInButton>(R.id.btn_googleSignIn).setOnClickListener {
            d(TAG, "Button google login pressed")
            firebaseSignInWithGoogle()
        }

        findViewById<LoginButton>(R.id.btn_facebookSignIn).setOnClickListener {
            d(TAG, "Button facebook login pressed")
            firebaseSignInWithFacebook()
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            if (findViewById<TextView>(R.id.tf_email).text.toString().isNotEmpty() && findViewById<TextView>(R.id.tf_password).text.toString().isNotEmpty()){
                d(TAG, "Text filled")
                firebaseAuthWithEmailAndPassword(findViewById<TextView>(R.id.tf_email).text.toString(), findViewById<TextView>(R.id.tf_password).text.toString())
                if (firebaseCheckUserStatus()) {
                    gotoProfilePage()
                } else {
                    d(TAG, "Not logged")
                }
            } else {
                d(TAG, "Text not filled")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            gotoProfilePage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                d(TAG, "firebaseAuthWithGoogle: $account.id")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                d(TAG, "Google sign in failed: $e")
            }
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }


    // Functions

    // [START goto_login_page]
    private fun gotoProfilePage(){
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]


    // [START auth_status]
    private fun firebaseCheckUserStatus(): Boolean {
        return if (auth.currentUser != null) {
            d(TAG, "Sign-in status: Logged \n Info ${auth.currentUser!!.email}")
            true
        } else {
            d(TAG, "Sign-in status: Not logged")
            false
        }

    }
    // [START auth_with_email&Password]
    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task->
                if(task.isSuccessful) {
                    d(TAG, "SignIn with email and password: Success!")
                    gotoProfilePage()
                } else {
                    d(TAG, "SignIn with email and password: Failed - $task")
                }
            }
    }
    // [END auth_with_email&Password]

    // [START auth_anonymously]
    private fun firebaseAuthAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    d(TAG, "SignIn anonymously: Success!")
                    gotoProfilePage()
                } else {
                    d(TAG, "SignIn anonymously: Failed - $task")
                }
            }
    }
    // [END auth_anonymously]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        d(TAG, "signInWithCredential:success")
                        gotoProfilePage()
                    } else {
                        // If sign in fails, display a message to the user.
                        d(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
    }

    private fun firebaseSignInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END auth_with_google]

    // [START auth_with_facebook]
    private fun firebaseSignInWithFacebook() {

        findViewById<LoginButton>(R.id.btn_facebookSignIn).registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                d(TAG, "facebook:onError", error)
            }
        })
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    d(TAG, "signInWithCredential:success")
                    gotoProfilePage()
                } else {
                    // If sign in fails, display a message to the user.
                    w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    // [END auth_with_facebook]

    // [START auth_password_forgotten]
    private fun firebasePasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    // Password reset sent
                    d(TAG, "Email reset: sent")
                } else {
                    // If sign in fails, display a message to the user.
                    d(TAG, "Email reset: failure", task.exception)
                }
            }
    }
    // [END auth_password_forgotten]


    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }
}