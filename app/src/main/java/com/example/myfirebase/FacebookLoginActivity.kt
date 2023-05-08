package com.example.myfirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirebase.databinding.ActivityFacebookLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import org.json.JSONException
import org.json.JSONObject
import java.util.Arrays


class FacebookLoginActivity : AppCompatActivity() {

    lateinit var facebookBinding: ActivityFacebookLoginBinding
    lateinit var callbackManager: CallbackManager
    var Email="email"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        facebookBinding = ActivityFacebookLoginBinding.inflate(layoutInflater)
        setContentView(facebookBinding.root)

        initView()
    }

    private fun initView() {


        callbackManager = CallbackManager.Factory.create()
        facebookBinding.btnLogin.setReadPermissions(Arrays.asList(Email))

        // Callback registration
        facebookBinding.btnLogin.registerCallback(
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



                    val credential:AuthCredential=FacebookAuthProvider.getCredential(result.accessToken.token)

                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}