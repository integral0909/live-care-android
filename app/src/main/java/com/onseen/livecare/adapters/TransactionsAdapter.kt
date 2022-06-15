package com.onseen.livecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionStatus
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionType
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate

class TransactionsAdapter(
    private val context: Context?,
    private val dataSource: MutableList<TransactionDataModel>
): RecyclerView.Adapter<TransactionsAdapter.TransactionRowViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)
    var itemClickListener: RowItemClickListener<TransactionDataModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): TransactionRowViewHolder {
        return TransactionRowViewHolder(
            mInflater.inflate(R.layout.row_transaction, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(viewHolder: TransactionRowViewHolder, position: Int) {
        viewHolder.bindTo(dataSource[position], context!!)
        viewHolder.itemView.setOnClickListener {
            itemClickListener?.onClickedRowItem(dataSource[position], position)
        }
    }

    class TransactionRowViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val imgTransactionType: ImageView = itemView.findViewById(R.id.imgTransactionType)
        private val txtTransactionType: TextView = itemView.findViewById(R.id.txtTransactionType)
        private val txtDateTime: TextView = itemView.findViewById(R.id.txtDateTime)
        private val txtAccount: TextView = itemView.findViewById(R.id.txtAccount)
        private val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        private val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
        private val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)

        fun bindTo(transaction: TransactionDataModel, context: Context) {
            txtTitle.text = transaction.refConsumer.szName
            txtTransactionType.text = transaction.enumType.getBeautifiedText()
            txtAccount.text = transaction.refAccount.szName
            txtAmount.text = String.format("$%.02f", transaction.fAmount)
            txtStatus.text = transaction.enumStatus.value
            txtDateTime.text = UtilsDate.getStringFromDateTimeWithFormat(transaction.dateUpdatedAt, EnumDateTimeFormat.hhmma_MMMd.value, null)

            if(transaction.enumType == EnumTransactionType.DEBIT) {
                this.imgTransactionType.setImageResource(R.mipmap.ic_withdrawal)
            } else if(transaction.enumType == EnumTransactionType.CREDIT) {
                this.imgTransactionType.setImageResource(R.mipmap.ic_deposit)
            } else {

            }

            if(transaction.enumStatus == EnumTransactionStatus.PENDING) {
                this.imgStatus.setImageResource(R.mipmap.ic_pending)
            } else if(transaction.enumStatus == EnumTransactionStatus.SUBMITTED) {
                this.imgStatus.setImageResource(R.mipmap.ic_submitted)
            } else if(transaction.enumStatus == EnumTransactionStatus.APPROVED) {
                this.imgStatus.setImageResource(R.mipmap.ic_approved)
            } else {

            }

            if(transaction.enumStatus == EnumTransactionStatus.PENDING) {
                this.itemView.setBackgroundResource(R.drawable.cell_background_red)
            } else {
                this.itemView.setBackgroundResource(R.drawable.cell_background_white)
            }
        }
    }
}