package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.unidevteam.cookingapp.activities.Home.HomeActivity
import com.unidevteam.cookingapp.models.CAUser
import com.unidevteam.cookingapp.services.DBManager
import com.unidevteam.cookingapp.util.SyntaxManager

class LoginActivity : AppCompatActivity() {

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
            Log.d(TAG, "Label guest pressed")
            firebaseAuthAnonymously()
        }

        findViewById<TextView>(R.id.lb_passwordForgotten).setOnClickListener {
            Log.d(TAG, "Password reset pressed")
            if (findViewById<TextView>(R.id.tf_email).text.isNotEmpty()) {
                // formatting check
                if (SyntaxManager.emailValidator(findViewById<TextView>(R.id.tf_email).text.toString())) {
                    firebasePasswordReset(findViewById<TextView>(R.id.tf_email).text.toString())
                } else {
                    Toast.makeText(this, "Email not formatted correctly", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Email field not filled", Toast.LENGTH_SHORT).show()
            }

        }

        findViewById<SignInButton>(R.id.btn_googleSignIn).setOnClickListener {
            Log.d(TAG, "Button google login pressed")
            firebaseSignInWithGoogle()
        }

        findViewById<LoginButton>(R.id.btn_facebookSignIn).setOnClickListener {
            Log.d(TAG, "Button facebook login pressed")
            firebaseSignInWithFacebook()
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            if (findViewById<TextView>(R.id.tf_email).text.toString().isNotEmpty() && findViewById<TextView>(
                    R.id.tf_password
                ).text.toString().isNotEmpty()){
                Log.d(TAG, "Text filled")
                firebaseAuthWithEmailAndPassword(findViewById<TextView>(R.id.tf_email).text.toString(), findViewById<TextView>(
                    R.id.tf_password
                ).text.toString())
                if (firebaseCheckUserStatus()) {
                    gotoHomePage()
                } else {
                    Log.d(TAG, "Not logged")
                }
            } else {
                Log.d(TAG, "Text not filled")
                Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            gotoHomePage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnCompleteListener { task ->
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle: $account.id")
                    firebaseAuthWithGoogle(account.idToken!!)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "firebaseAuthWithGoogle ERROR: ${e.message.toString()}")
                }
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }


    // Functions

    // [START goto_login_page]
    private fun gotoHomePage(){
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
    }
    // [END goto_login_page]


    // [START auth_status]
    private fun firebaseCheckUserStatus(): Boolean {
        return if (auth.currentUser != null) {
            Log.d(TAG, "Sign-in status: Logged \n Info ${auth.currentUser!!.email}")
            true
        } else {
            Log.d(TAG, "Sign-in status: Not logged")
            false
        }

    }
    // [START auth_with_email&Password]
    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task->
                if(task.isSuccessful) {
                    Log.d(TAG, "SignIn with email and password: Success!")
                    gotoHomePage()
                } else {
                    Log.d(TAG, "SignIn with email and password: Failed - $task")
                    Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // [END auth_with_email&Password]

    // [START auth_anonymously]
    private fun firebaseAuthAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    Log.d(TAG, "SignIn anonymously: Success!")
                    gotoHomePage()
                } else {
                    Log.d(TAG, "SignIn anonymously: Failed - $task")
                    Toast.makeText(this, "Guest sinning failed ", Toast.LENGTH_SHORT).show()
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
                    Log.d(TAG, "signInWithCredential: success")

                    DBManager.getCurrentUserExtraInfo()
                        .addOnCompleteListener{ dSnapTask ->
                            if(dSnapTask.isSuccessful) {
                                if(!dSnapTask.result.exists()) {
                                    val caUser : CAUser = CAUser(
                                        imageURL = auth.currentUser!!.photoUrl.toString(),
                                        email = auth.currentUser!!.email!!,
                                        name = auth.currentUser!!.displayName!!.split(' ')[0],
                                        surname = auth.currentUser!!.displayName!!.split(' ')[1],
                                        bio = "Ciao! Sono un nuovo cuoco di CookingApp!"
                                    )

                                    DBManager.saveUserInfo(caUser)
                                        .addOnCompleteListener {
                                            gotoHomePage()
                                        }
                                } else {
                                    gotoHomePage()
                                }
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential: failure", task.exception)
                    Toast.makeText(this, "Login with Google failed", Toast.LENGTH_SHORT).show()
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

        findViewById<LoginButton>(R.id.btn_facebookSignIn).registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    DBManager.getCurrentUserExtraInfo()
                        .addOnCompleteListener{ dSnapTask ->
                            if(dSnapTask.isSuccessful) {
                                if(!dSnapTask.result.exists()) {
                                    val caUser : CAUser = CAUser(
                                        imageURL = auth.currentUser!!.photoUrl.toString(),
                                        email = auth.currentUser!!.email!!,
                                        name = auth.currentUser!!.displayName!!.split(' ')[0],
                                        surname = auth.currentUser!!.displayName!!.split(' ')[1],
                                        bio = "Ciao! Sono un nuovo cuoco di CookingApp!"
                                    )

                                    DBManager.saveUserInfo(caUser)
                                        .addOnCompleteListener {
                                            gotoHomePage()
                                        }
                                }
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Login with Facebook failed", Toast.LENGTH_SHORT).show()
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
                    Log.d(TAG, "Email reset: sent")
                    Toast.makeText(this, "Email reset sent", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "Email reset: failure", task.exception)
                }
            }
    }
    // [END auth_password_forgotten]


    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_SIGN_IN = 9001
    }
}