package com.example.myfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterClass(
    var onEditClick: (StudentModelClass) -> Unit,
    var onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<AdapterClass.MyViewHolder>() {
    private var list = ArrayList<StudentModelClass>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNameDisplay: TextView = itemView.findViewById(R.id.txtNameDisplay)
        var txtEmailDisplay: TextView = itemView.findViewById(R.id.txtEmailDisplay)
        var txtMobileDisplay: TextView = itemView.findViewById(R.id.txtMobileDisplay)
        var txtAddressDisplay: TextView = itemView.findViewById(R.id.txtAddressDisplay)
        var imgEdit: ImageView = itemView.findViewById(R.id.imgEdit)
        var imgDelete: ImageView = itemView.findViewById(R.id.imgDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.data_dispaly_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            txtNameDisplay.text = list[position].name
            txtEmailDisplay.text = list[position].email
            txtMobileDisplay.text = list[position].mobile
            txtAddressDisplay.text = list[position].address

            imgEdit.setOnClickListener {
                onEditClick.invoke(list[position])

            }
            imgDelete.setOnClickListener {
                onDeleteClick.invoke(list[position].id)

            }
        }

    }

    fun updateList(studentList: java.util.ArrayList<StudentModelClass>) {
        list = studentList
        notifyDataSetChanged()
    }
}