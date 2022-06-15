package com.onseen.livecare.views

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.onseen.livecare.R

class AddItemHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
    private val btnAdd: Button = itemView.findViewById(R.id.btnAdd)

    fun setTitle(title: String?) {
        txtTitle.text = title
    }

    fun setTitle(titleId: Int) {
        txtTitle.setText(titleId)
    }

    fun setOnClickAddButtonListener(listener: View.OnClickListener) {
        btnAdd.setOnClickListener(listener)
    }

    fun hideAddButton() {
        btnAdd.visibility = View.GONE
    }
}