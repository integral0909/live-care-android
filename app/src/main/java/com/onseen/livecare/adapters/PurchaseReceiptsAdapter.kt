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
import com.onseen.livecare.activities.LivecareApp
import com.onseen.livecare.activities.MainActivity
import com.onseen.livecare.fragments.main.Transactions.ViewModel.TransactionDetailsViewModel
import com.onseen.livecare.interfaces.PurchaseReceiptsListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PurchaseReceiptsAdapter(private val context: Context?, private val arrayTransactions: ArrayList<TransactionDetailsViewModel>): RecyclerView.Adapter<PurchaseReceiptsAdapter.PurchaseReceiptsHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(this.context)

    var itemClickListener: PurchaseReceiptsListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): PurchaseReceiptsHolder {
        return PurchaseReceiptsHolder(mInflater.inflate(R.layout.row_transaction_detail, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayTransactions.size
    }

    override fun onBindViewHolder(viewHolder: PurchaseReceiptsHolder, position: Int) {
        val transaction = this.arrayTransactions[position]
        val selectedConsumer = transaction.modelConsumer

        // Select Consumer
        val arrayConsumers = ConsumerManager.sharedInstance().arrayConsumers
        val arrayConsumerNames: MutableList<String> = mutableListOf()
        arrayConsumerNames.add("Select Consumer")
        arrayConsumerNames.addAll(arrayConsumers.map { it.szName} )

        val adapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayConsumerNames)
        viewHolder.edtConsumer.adapter = adapter
        viewHolder.edtConsumer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, indexConsumer: Int, id: Long) {
                if(indexConsumer == 0) return
                itemClickListener?.didTransactionDetailsConsumerSelected(indexConsumer - 1, position)
                didTransactionDetailsConsumerSelected(indexConsumer - 1, viewHolder, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        var index = 0
        var found = false
        for(c in arrayConsumers) {
            if(selectedConsumer != null && c.id!! == selectedConsumer.id) {
                viewHolder.edtConsumer.setSelection(index + 1, false)
                found = true
                break
            }
            index += 1
        }

        if (!found) {
            viewHolder.edtConsumer.setSelection(0, false)
        }

        if(transaction.fAmount > 0) {
            viewHolder.edtAmount.setText(String.format("%.2f", transaction.fAmount))
        }
        viewHolder.edtAmount.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amount: Double = UtilsString.parseDouble(viewHolder.edtAmount.text.toString(), 0.0)
                transaction.fAmount = amount
                itemClickListener?.didTransactionDetailsAmountChanged(amount, position)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        viewHolder.btnDelete.setOnClickListener {
            itemClickListener?.didTransactionDetailsDeleteClick(position)
        }
    }

    private fun didTransactionDetailsConsumerSelected(indexConsumer: Int, viewHolder: PurchaseReceiptsHolder, position: Int) {
        val consumer = ConsumerManager.sharedInstance().arrayConsumers[indexConsumer]

        (LivecareApp.currentActivity() as MainActivity).showProgressHUD()

        FinancialAccountManager.sharedInstance().requestGetAccountsForConsumer(consumer, false, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    (LivecareApp.currentActivity() as MainActivity).hideProgressHUD()

                    if(responseDataModel.isSuccess()) {
                        val arrayAccounts: ArrayList<String> = ArrayList()
                        arrayAccounts.add("Select Account")
                        for(account in consumer.arrayAccounts!!) {
                            arrayAccounts.add(account.szName)
                        }

                        val accountAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayAccounts)
                        viewHolder.edtAccount.adapter = accountAdapter
                        viewHolder.edtAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, accountIndex: Int, id: Long) {
                                if(accountIndex == 0) return
                                itemClickListener!!.didTransactionDetailsAccountSelected(accountIndex - 1, position)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }

                        var index = 0
                        var found = false
                        val transaction = arrayTransactions[position]
                        val selectedAccount = transaction.getModelAccount()
                        for(c in consumer.arrayAccounts!!) {
                            if(selectedAccount == null) break
                            if(c.id!! == selectedAccount.id) {
                                viewHolder.edtAccount.setSelection(index + 1, false)
                                found = true
                                break
                            }
                            index += 1
                        }

                        if (!found) {
                            viewHolder.edtAccount.setSelection(0, false)
                        }

                    } else {
                        val arrayAccounts: ArrayList<String> = ArrayList()
                        arrayAccounts.add("Select Account")
                        val accountAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrayAccounts)
                        viewHolder.edtAccount.setAdapter(accountAdapter)
                        viewHolder.edtAccount.setSelection(0, false)
                    }
                }
            }
        })
    }

    class PurchaseReceiptsHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val edtConsumer: Spinner = itemView.findViewById(R.id.edtConsumer)
        val edtAccount: Spinner = itemView.findViewById(R.id.edtAccount)
        val edtAmount: EditText = itemView.findViewById(R.id.edtAmount)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

    }
}