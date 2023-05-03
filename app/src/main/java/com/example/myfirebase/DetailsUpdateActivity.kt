package com.example.myfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirebase.databinding.ActivityDetailsUpdateBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailsUpdateActivity : AppCompatActivity() {

    lateinit var updateBinding: ActivityDetailsUpdateBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBinding = ActivityDetailsUpdateBinding.inflate(layoutInflater)
        setContentView(updateBinding.root)

        initView()
    }

    private fun initView() {
        updateBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        id=intent.getStringExtra("id").toString()
        updateBinding.edtNameUpdate.setText(intent.getStringExtra("name"))
        updateBinding.edtEmailUpdate.setText(intent.getStringExtra("email"))
        updateBinding.edtMobileUpdate.setText(intent.getStringExtra("mobile"))
        updateBinding.edtAddressUpdate.setText(intent.getStringExtra("address"))

        firebaseDatabase = FirebaseDatabase.getInstance()

        updateBinding.btnUpdateRecord.setOnClickListener {

            var data = StudentModelClass(
                id,
                updateBinding.edtNameUpdate.text.toString(),
                updateBinding.edtEmailUpdate.text.toString(),
                updateBinding.edtMobileUpdate.text.toString(),
                updateBinding.edtAddressUpdate.text.toString()
            )
            firebaseDatabase.reference.child("StudentTb").child(id).setValue(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Record Updated Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.addOnFailureListener {
                    Log.e("TAG", "initView: " + it.message)
                }

            var i = Intent(this, DataDisplayActivity::class.java)
            startActivity(i)
        }


    }


}