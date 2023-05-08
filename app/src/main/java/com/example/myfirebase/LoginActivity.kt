package com.example.myfirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirebase.databinding.ActivityFacebookLoginBinding
import com.example.myfirebase.databinding.ActivityLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONObject
import java.util.Arrays


class LoginActivity : AppCompatActivity() {

    lateinit var  loginBinding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var callbackManager: CallbackManager
    var Email="email"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        initView()
    }

    private fun initView() {

//        // Initialize firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()
//        // Initialize firebase user
//        val firebaseUser = firebaseAuth.currentUser


        // Initialize sign in options the client-id is copied form google-services.json file

        // Initialize sign in options the client-id is copied form google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("167582794627-2372p5q5ih8ukjl0i6qnesin8i751j49.apps.googleusercontent.com")
            .requestEmail()
            .build()


        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        loginBinding.btnGoogleLogin.setOnClickListener { // Initialize sign in intent
            val intent: Intent = googleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(intent, 100)
        }

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }


//facebook Login
        callbackManager = CallbackManager.Factory.create()
        loginBinding.btnLoginButton.setReadPermissions(Arrays.asList(Email))

        // Callback registration
        loginBinding.btnLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {

                }

                override fun onSuccess(result: LoginResult) {

                    var request = GraphRequest.newMeRequest(result.accessToken,
                        object : GraphRequest.GraphJSONObjectCallback {
                            override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {

                                var email = obj?.getString("email")

                                Log.e("TAG", "onCompleted:"+email)

                            }
                        })

                    val parameters=Bundle()
                    parameters.putString("fields","id,name,email,gender,birthday")
                    request.parameters=parameters
                    request.executeAsync()



                    val credential:AuthCredential= FacebookAuthProvider.getCredential(result.accessToken.token)
                    Log.e("Token", "onSuccess: "+result.accessToken.token)
                    // Check credential
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener{
                            // Check condition
                            if (it.isSuccessful) {
                                // When task is successful redirect to profile activity
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                                // Display Toast
                                Toast.makeText(this@LoginActivity, "Firebase authentication successful", Toast.LENGTH_SHORT).show()
                            } else {
                                // When task is unsuccessful display Toast
                                Toast.makeText(this@LoginActivity, "Authentication Failed :", Toast.LENGTH_SHORT).show()
                            }
                        }

                }

            })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            val signInAccountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            // check condition
            if (signInAccountTask.isSuccessful) {
                // When google sign in successful initialize string
                val s = "Google sign in successful"
                // Display Toast
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                            googleSignInAccount.idToken, null
                        )
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                // Check condition
                                if (task.isSuccessful) {
                                    // When task is successful redirect to profile activity
                                    startActivity(
                                        Intent(
                                            this,
                                            MainActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    // Display Toast
                                    Toast.makeText(this, "Firebase authentication successful", Toast.LENGTH_SHORT).show()
                                } else {
                                    // When task is unsuccessful display Toast
                                    Toast.makeText(this, "Authentication Failed :", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }

}