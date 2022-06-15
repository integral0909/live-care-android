package com.onseen.livecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Transactions.ViewModel.PurchaseViewModel
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate
import java.util.*
import kotlin.collections.ArrayList

class PurchaseSummaryAdapter(val context: Context, val vmPurchase: PurchaseViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): RecyclerView.ViewHolder {
        return when (itemType) {
            0 -> PurchaseSummaryHeaderViewHolder(
                mInflater.inflate(R.layout.view_purchase_summary_info, parent, false)
            )
            1 -> PurchaseSummaryImagesSignatureViewHolder(
                mInflater.inflate(R.layout.view_purchase_summary_images_signature, parent, false)
            )
            else -> PurchaseSignatureRowViewHolder(
                mInflater.inflate(R.layout.row_purchase_signature, parent, false)
            )

        }
    }

    override fun getItemCount(): Int {
        if(vmPurchase.hasReceiptPhotos)
            return vmPurchase.arrayTransactionDetails.size + 2
        else
            return vmPurchase.arrayTransactionDetails.size + 1
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is PurchaseSummaryHeaderViewHolder -> {
                viewHolder.bindTo(vmPurchase, position)
            }
            is PurchaseSummaryImagesSignatureViewHolder -> {
                viewHolder.bindTo(context, vmPurchase, position)
            }
            else -> {
                (viewHolder as PurchaseSignatureRowViewHolder).bindTo(context, vmPurchase, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            vmPurchase.arrayTransactionDetails.size + 1 -> 1
            else -> 2
        }
    }

    class PurchaseSummaryHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtTotalAmount: TextView = itemView.findViewById(R.id.txtTotalAmount)
        private val edtDate: TextView = itemView.findViewById(R.id.edtDate)
        private val edtMerchant: TextView = itemView.findViewById(R.id.edtMerchant)
        private val edtDescription: TextView = itemView.findViewById(R.id.edtDescription)

        fun bindTo(vmPurchase: PurchaseViewModel, position: Int) {
            txtTotalAmount.text = String.format("$%.02f", vmPurchase.getTotalAmount())
            edtDate.text = UtilsDate.getStringFromDateTimeWithFormat(vmPurchase.date, EnumDateTimeFormat.EEEEMMMMdyyyy.value, null)
            edtMerchant.text = vmPurchase.szMerchant
            edtDescription.text = vmPurchase.szDescription
        }
    }

    class PurchaseSignatureRowViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val edtSpend: TextView = itemView.findViewById(R.id.edtSpend)
        private val edtRedeposit: TextView = itemView.findViewById(R.id.edtRedeposit)
        private val edtPending: TextView = itemView.findViewById(R.id.edtPending)

        fun bindTo(context: Context, vmPurchase: PurchaseViewModel, position: Int) {
            val transaction = vmPurchase.arrayTransactionDetails[position-1]
            if(transaction.getModelAccount() == null) return

            txtTitle.text = String.format(Locale.US, transaction.getConsumerName() + " - " + transaction.getAccountName())
            edtSpend.text = String.format("$%.02f", transaction.fAmount)
            edtRedeposit.text = String.format("$%.02f", transaction.fRemainingDeposit)

            if(transaction.getModelAccount()!!.enumType == EnumFinancialAccountType.CASH) {
                val pending = transaction.getPendingAmount() - transaction.fAmount - transaction.fRemainingDeposit
                edtPending.text = String.format("$%.02f", pending)
            } else {
                edtPending.text = "$0.00"
            }
        }
    }

    class PurchaseSummaryImagesSignatureViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val gridView: GridView = itemView.findViewById(R.id.gridView)

        fun bindTo(context: Context, vmPurchase: PurchaseViewModel, position: Int) {
            val arrayGrid: ArrayList<GridItem> = ArrayList()
            for(i in vmPurchase.arrayReceiptPhotos) {
                val item = GridItem()
                item.imgFile = i
                item.type = EnumGripRowType.ROW_IMAGE
                arrayGrid.add(item)
            }

            val gridImageAdapter = GridImageAdapter(context, arrayGrid)
            gridImageAdapter.itemClickListener = object: RowItemClickListener<GridItem> {
                override fun onClickedRowItem(obj: GridItem, position: Int) {

                }
            }
            gridView.adapter = gridImageAdapter
        }
    }
}