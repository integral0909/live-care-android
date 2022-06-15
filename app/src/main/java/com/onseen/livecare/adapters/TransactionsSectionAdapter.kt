package com.onseen.livecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Transactions.TransactionsSectionModel
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate

class TransactionsSectionAdapter(
    private val context: Context?,
    private val dataSource: ArrayList<TransactionsSectionModel>
): RecyclerView.Adapter<TransactionsSectionAdapter.TransactionsSectionRowViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)
    var itemClickListener: RowItemClickListener<TransactionDataModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): TransactionsSectionRowViewHolder {
        return TransactionsSectionRowViewHolder(mInflater.inflate(R.layout.row_transactions_section, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(viewHolder: TransactionsSectionRowViewHolder, position: Int) {
        viewHolder.bindTo(dataSource[position], context!!, itemClickListener)
    }

    class TransactionsSectionRowViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val sectionListView : RecyclerView = itemView.findViewById(R.id.transaction_section_list)

        fun bindTo(transaction: TransactionsSectionModel, context: Context, itemClickListener: RowItemClickListener<TransactionDataModel>?) {
            txtTitle.text = UtilsDate.getStringFromDateTimeWithFormat(transaction.sectionDate, EnumDateTimeFormat.EEEEMMMMdyyyy.value, null)

            val consumersAdapter = TransactionsAdapter(context, transaction.arrayTransactions)
            consumersAdapter.itemClickListener = object: RowItemClickListener<TransactionDataModel> {
                override fun onClickedRowItem(obj: TransactionDataModel, position: Int) {
                    itemClickListener?.onClickedRowItem(transaction.arrayTransactions[position], position)
                }
            }

            val viewManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            sectionListView.apply {
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                layoutManager = viewManager
                adapter = consumersAdapter
            }
        }
    }
}