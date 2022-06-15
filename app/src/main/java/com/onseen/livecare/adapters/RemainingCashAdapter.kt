package com.onseen.livecare.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Transactions.ViewModel.TransactionDetailsViewModel
import com.onseen.livecare.interfaces.ReceiptItemsListener

class RemainingCashAdapter(val context: Context, val arrayItems: ArrayList<TransactionDetailsViewModel>): RecyclerView.Adapter<RemainingCashAdapter.DepositItemViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)
    var itemClickListener: ReceiptItemsListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): DepositItemViewHolder {
        return DepositItemViewHolder(mInflater.inflate(R.layout.row_desposit_remaining, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayItems.size
    }

    override fun onBindViewHolder(viewHolder: DepositItemViewHolder, position: Int) {
        val item = arrayItems[position]
        viewHolder.bindTo(item, position, itemClickListener!!)
    }

    class DepositItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val edtCunsumer: TextView = itemView.findViewById(R.id.edtConsumer)
        private val edtAccount: TextView = itemView.findViewById(R.id.edtAccount)
        private val edtAmount: EditText = itemView.findViewById(R.id.edtAmount)

        fun bindTo(transaction: TransactionDetailsViewModel, position: Int, itemClickListener: ReceiptItemsListener) {

            edtCunsumer.setText(transaction.modelConsumer!!.szName)
            edtAccount.setText(transaction.getModelAccount()!!.szName)
            edtAmount.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    itemClickListener.didReceiptItemNameChanged(edtAmount.text.toString(), position)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
        }
    }
}