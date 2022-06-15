package com.onseen.livecare.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.ReceiptItemsListener

class ReceiptItemsAdapter(private val context: Context?, private val arrayReceipts: ArrayList<String>): RecyclerView.Adapter<ReceiptItemsAdapter.ConsumerViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)
    var itemClickListener: ReceiptItemsListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ConsumerViewHolder {
        return ConsumerViewHolder(
            mInflater.inflate(R.layout.row_receipt_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayReceipts.size
    }

    override fun onBindViewHolder(viewHolder: ConsumerViewHolder, position: Int) {
        viewHolder.bindTo(arrayReceipts[position], itemClickListener!!, position)
    }

    class ConsumerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtLabel)
        private val edtName: EditText = itemView.findViewById(R.id.edtName)
        private val btnRemove: Button = itemView.findViewById(R.id.btnDelete)

        fun bindTo(name: String, itemClickListener: ReceiptItemsListener, position: Int) {
            btnRemove.setOnClickListener {
                itemClickListener.didReceiptItemDeleteClick(position)
            }
            edtName.setText(name ?: "")
            edtName.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    itemClickListener.didReceiptItemNameChanged(edtName.text.toString(), position)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
        }
    }
}