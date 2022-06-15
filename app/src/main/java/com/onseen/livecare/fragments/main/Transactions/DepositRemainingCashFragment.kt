package com.onseen.livecare.fragments.main.Transactions


import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*

import com.onseen.livecare.R
import com.onseen.livecare.adapters.RemainingCashAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Patients.AuditDialogFragment
import com.onseen.livecare.fragments.main.Transactions.ViewModel.PurchaseViewModel
import com.onseen.livecare.fragments.main.Transactions.ViewModel.TransactionDetailsViewModel
import com.onseen.livecare.interfaces.ReceiptItemsListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.Transaction.TransactionManager
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.android.synthetic.main.fragment_deposit_remaining_cash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.max
import java.util.*
import kotlin.collections.ArrayList

class DepositRemainingCashFragment : BaseMainHomeFragment(), ReceiptItemsListener {

    private var vmPurchaseViewModel: PurchaseViewModel? = null
    var arrayCashDetails: ArrayList<TransactionDetailsViewModel> = ArrayList()

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var remainingCashAdapter: RemainingCashAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deposit_remaining_cash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle("Deposit Remaining", false)

        refreshListView()
        switchDeposit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                recyclerView.visibility = View.VISIBLE
            else
                recyclerView.visibility = View.GONE

            this.vmPurchaseViewModel!!.hasRemainingDeposit = isChecked
            refreshListView()
        }
    }

    private fun refreshListView() {
        if(this.vmPurchaseViewModel == null) return

        this.arrayCashDetails.clear()
        this.arrayCashDetails.addAll(this.vmPurchaseViewModel!!.getTransactionDetailsForCashAccount())
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        remainingCashAdapter = RemainingCashAdapter(activity!!, this.arrayCashDetails)
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = remainingCashAdapter
        }
        remainingCashAdapter.itemClickListener = this
    }

    private fun requestReloadTransactions() {
        showProgressHUD()
        TransactionManager.sharedInstance().requestGetTransactions(object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    checkRemainingDeposit()
                }
            }
        })
    }

    private fun checkRemainingDeposit() {
        for(transaction in this.arrayCashDetails) {
            if(transaction.modelConsumer == null || transaction.getModelAccount() == null) continue

            val amountPending = transaction.getPendingAmount()
            val remaining = max(amountPending - transaction.fAmount, 0.0)
            if(remaining != transaction.fRemainingDeposit && transaction.getModelAccount()!!.enumType == EnumFinancialAccountType.CASH) {
                presentErrorFragment(transaction)
                return
            }
        }

        gotoSummaryFragment()
    }

    private fun gotoSummaryFragment() {
        mainFragment.addFragment(PurchaseSummaryFragment.newInstance(this.vmPurchaseViewModel))
    }

    private fun presentErrorFragment(transaction: TransactionDetailsViewModel) {
        val ft = fragmentManager?.beginTransaction() ?: return
        val auditDialogFragment = AuditDialogFragment()
        auditDialogFragment.isFromDepositRemainingFragment = true
        auditDialogFragment.strWarningTitle = String.format(Locale.US, "Warning: \nThe Redeposit amount for " + transaction.modelConsumer!!.szName + " does not match the pending amount.\nPlease recount the cash.")
        auditDialogFragment.show(ft, "AuditDialog")
    }

    override fun didReceiptItemDeleteClick(indexRow: Int) {

    }

    override fun didReceiptItemNameChanged(name: String, indexRow: Int) {
        if(this.vmPurchaseViewModel == null) return

        val transaction = this.arrayCashDetails[indexRow]
        transaction.fRemainingDeposit = UtilsString.parseDouble(name, 0.0)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
            showBackButton()
        } else {
            setHasOptionsMenu(false)
            hideBackButton()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemNext = menu.findItem(R.id.next)
        itemNext.isVisible = true
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.next) {
            hideKeyboard()
            if(!this.vmPurchaseViewModel!!.hasRemainingDeposit) {
                gotoSummaryFragment()
            } else {
                requestReloadTransactions()
            }
            return true
        } else {
            if(item.itemId == android.R.id.home) {
                mainFragment.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun newInstance(purchase: PurchaseViewModel?) =
            DepositRemainingCashFragment().apply {
                arguments = Bundle().apply {
                    vmPurchaseViewModel = purchase
                }
            }
    }
}
