package com.onseen.livecare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.ConsumerAccountButtonListener
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel

class ConsumerAccountsAdapter(private val context: Context?,
                              private val dataSource: List<FinancialAccountDataModel>
): RecyclerView.Adapter<ConsumerAccountsAdapter.AccountViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)
    var buttonListener: ConsumerAccountButtonListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): AccountViewHolder {
        return AccountViewHolder(mInflater.inflate(R.layout.row_wallet, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(view: AccountViewHolder, position: Int) {
        view.bindTo(dataSource[position])
        view.btnNewTransaction.setOnClickListener {
            buttonListener?.onClickedNewTransaction(dataSource[position], position)
        }
        view.btnAudit.setOnClickListener {
            buttonListener?.onClickedAudit(dataSource[position], position)
        }
    }

    class AccountViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val txtAccountName: TextView = itemView.findViewById(R.id.txtAccount)
        private val txtCardNumber: TextView = itemView.findViewById(R.id.txtCardNumber)
        private val txtBalance: TextView = itemView.findViewById(R.id.txtBalance)
        private val txtCurrency: TextView = itemView.findViewById(R.id.txtCurrency)
        val btnNewTransaction: Button = itemView.findViewById(R.id.btnNewTransaction)
        val btnAudit: Button = itemView.findViewById(R.id.btnAuditAccount)

        fun bindTo(account: FinancialAccountDataModel) {
            txtAccountName.text = account.szName
            if(account.enumType == EnumFinancialAccountType.GIFT_CARD) {
                val cardNumber = account.szLast4
                txtCardNumber.text = cardNumber //String.format("(...%s)", cardNumber.subSequence(cardNumber.length - 4, 4))
            } else {
                txtCardNumber.text = ""
            }

            txtBalance.text = String.format("$%.02f", account.fBalance)
//            txtCurrency.text
        }
    }
}