package com.example.myfirebase

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirebase.databinding.ActivityDataDisplayBinding
import com.example.myfirebase.databinding.DeleteDialogBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataDisplayActivity : AppCompatActivity() {
    lateinit var displayBinding: ActivityDataDisplayBinding
    lateinit var firebaseDatabase: FirebaseDatabase
    var studentList = ArrayList<StudentModelClass>()
    lateinit var adapter: AdapterClass
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayBinding = ActivityDataDisplayBinding.inflate(layoutInflater)
        setContentView(displayBinding.root)
        initView()
    }

    private fun initView() {

        displayBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        firebaseDatabase = FirebaseDatabase.getInstance()

        setAdapter()
        firebaseDatabase.reference.child("StudentTb")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    studentList.clear()
                    for (i in snapshot.children) {
                        var data = i.getValue(StudentModelClass::class.java)
                        Log.e("TAG", "onDataChange: " + data?.name + data?.address)
                        data?.let { it1 -> studentList.add(it1) }
                    }
                    adapter.updateList(studentList)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    private fun setAdapter() {
        adapter = AdapterClass(this,{
            var editIntent = Intent(this, DetailsUpdateActivity::class.java)
            id = it.id
            editIntent.putExtra("id", id)
            editIntent.putExtra("name", it.name)
            editIntent.putExtra("email", it.email)
            editIntent.putExtra("mobile", it.mobile)
            editIntent.putExtra("address", it.address)
            startActivity(editIntent)
        }, {
            id = it
            deleteRecordFromDatabase()

        })
        var manger =
            LinearLayoutManager(this@DataDisplayActivity, LinearLayoutManager.VERTICAL, false)
        displayBinding.rcvDataDisplay.layoutManager = manger
        displayBinding.rcvDataDisplay.adapter = adapter
    }

    private fun deleteRecordFromDatabase() {

        var deleteDialog = Dialog(this)

        var dialogBinding=DeleteDialogBinding.inflate(layoutInflater)
        deleteDialog.setContentView(dialogBinding.root)

        dialogBinding.btnCanselDelete.setOnClickListener {
            deleteDialog.dismiss()
            Toast.makeText(this, "Cansel", Toast.LENGTH_SHORT).show()
        }
        dialogBinding.btnDelete.setOnClickListener {
            firebaseDatabase.reference.child("StudentTb").child(id).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Record Deleted Successfully", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.e("TAG", "initView: " + it.message)
                }
            deleteDialog.dismiss()
        }

        deleteDialog. window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        deleteDialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

    }
}