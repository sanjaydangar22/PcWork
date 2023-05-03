package com.example.myfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myfirebase.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var firebaseDatabase: FirebaseDatabase

    var studentList:ArrayList<StudentModelClass> =ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initView()
    }

    private fun initView() {
        firebaseDatabase = FirebaseDatabase.getInstance()


        mainBinding.btnInsertRecord.setOnClickListener {
            var key = firebaseDatabase.reference.child("StudentTb").push().key ?: ""
            var data = StudentModelClass(
                key,
                mainBinding.edtName.text.toString(),
                mainBinding.edtEmail.text.toString(),
                mainBinding.edtMobile.text.toString(),
                mainBinding.edtAddress.text.toString()
            )
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
        mainBinding.btnDisplayRecord.setOnClickListener {

            var i= Intent(this@MainActivity,DataDisplayActivity::class.java)
            startActivity(i)
        }
    }
}