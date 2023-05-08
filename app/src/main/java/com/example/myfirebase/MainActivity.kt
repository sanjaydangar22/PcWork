package com.example.myfirebase

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirebase.databinding.ActivityMainBinding
import com.example.myfirebase.databinding.SelecteImageDialogBinding
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID


class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var firebaseDatabase: FirebaseDatabase

    var PICK_IMAGE_REQUEST = 100
    var CAMERA_REQUEST = 200

    lateinit var storageReference: StorageReference

    lateinit var filePath: Uri
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    var studentList: ArrayList<StudentModelClass> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initView()
        signOut()
    }

    private fun signOut() {
        // Initialize firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()
        // Firebase sign out
//        firebaseAuth.signOut()

        // Google sign out
        mainBinding.btnGoogleLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
//
//        mainBinding.btnGoogleLogout.setOnClickListener { view ->
//            // Sign out from google
//            googleSignInClient.signOut().addOnCompleteListener { task ->
//                // Check condition
//                if (task.isSuccessful) {
//                    // When task is successful sign out from firebase
//                    firebaseAuth.signOut()
//                    // Display Toast
//                    Toast.makeText(
//                        applicationContext,
//                        "Logout successful",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    // Finish activity
//                    finish()
//                }
//            }
//        }
    }

    private fun initView() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        // get the Firebase  storage reference

        storageReference = FirebaseStorage.getInstance().reference

        mainBinding.btnSelectImage.setOnClickListener {

            selectImage()
        }
        mainBinding.btnUploadImage.setOnClickListener {

            uploadImage()
        }

        mainBinding.btnInsertRecord.setOnClickListener {


            var name = mainBinding.edtName.text.toString()
            var email = mainBinding.edtEmail.text.toString()
            var mobile = mainBinding.edtMobile.text.toString()
            var address = mainBinding.edtAddress.text.toString()
            var key = firebaseDatabase.reference.child("StudentTb").push().key ?: ""
            var data = StudentModelClass(key, name, email, mobile, address)

            if (name.isEmpty()) {
                Toast.makeText(this, "please Enter Name", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "please Enter Email", Toast.LENGTH_SHORT).show()
            } else if (mobile.isEmpty()) {
                Toast.makeText(this, "please Enter Mobile", Toast.LENGTH_SHORT).show()
            } else if (address.isEmpty()) {
                Toast.makeText(this, "please Enter Address", Toast.LENGTH_SHORT).show()
            } else {
                firebaseDatabase.reference.child("StudentTb").child(key).setValue(data)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Log.e("TAG", "initView: " + it.message)
                    }
                mainBinding.edtName.setText("").toString()
                mainBinding.edtEmail.setText("").toString()
                mainBinding.edtMobile.setText("").toString()
                mainBinding.edtAddress.setText("").toString()
            }

        }
        mainBinding.btnDisplayRecord.setOnClickListener {

            var i = Intent(this@MainActivity, DataDisplayActivity::class.java)
            startActivity(i)
        }
    }


    private fun selectImage() {


        var selectDialog = Dialog(this)

        var dialogBinding= SelecteImageDialogBinding.inflate(layoutInflater)
        selectDialog.setContentView(dialogBinding.root)

        dialogBinding.linCancel.setOnClickListener {
            selectDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.imgGallery.setOnClickListener {
        galleryView()
            selectDialog.dismiss()
        }
        dialogBinding.imgCamera.setOnClickListener {
            cameraView()
            selectDialog.dismiss()
        }

        selectDialog. window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        selectDialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        selectDialog.show()


    }

    private fun cameraView() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun galleryView() {

        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            filePath = data?.data!!
            mainBinding.imgShow.setImageURI(filePath)
        }
        if (requestCode === CAMERA_REQUEST && resultCode === RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            mainBinding.imgShow.setImageBitmap(imageBitmap)
        }
    }


    // UploadImage method
    private fun uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "images/"
                            + UUID.randomUUID().toString()
                )

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, "Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded " + progress.toInt() + "%"
                    )
                }
        }
    }
}