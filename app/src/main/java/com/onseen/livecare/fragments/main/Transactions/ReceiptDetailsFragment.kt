package com.onseen.livecare.fragments.main.Transactions


import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*

import com.onseen.livecare.R
import com.onseen.livecare.adapters.ReceiptItemsAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Transactions.ViewModel.PurchaseViewModel
import com.onseen.livecare.fragments.main.Transactions.ViewModel.TransactionDetailsViewModel
import com.onseen.livecare.interfaces.ReceiptItemsListener
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.android.synthetic.main.fragment_receipt_items.*

class ReceiptDetailsFragment : BaseMainHomeFragment(), ReceiptItemsListener {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private var vmPurchase: PurchaseViewModel? = null

    private lateinit var receiptItemsAdapter: ReceiptItemsAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receipt_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle("Receipt", false)

        btnAdd.setOnClickListener {
            this.vmPurchase!!.vmManualReceipt.arrayNames.add("")
            setupRecyclerView()
        }

        refreshFields()
    }

    private fun refreshFields() {
        if(this.vmPurchase == null) return

        this.vmPurchase!!.vmManualReceipt.szVendor = this.vmPurchase!!.szMerchant

        // Current Date
        edtDate.setText(UtilsDate.getStringFromDateTimeWithFormat(this.vmPurchase!!.date, EnumDateTimeFormat.EEEEMMMMdyyyy.value, null))
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        receiptItemsAdapter = ReceiptItemsAdapter(activity!!, this.vmPurchase!!.vmManualReceipt.arrayNames)
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = receiptItemsAdapter
        }
        receiptItemsAdapter.itemClickListener = this
    }

    override fun didReceiptItemDeleteClick(indexRow: Int) {
        if(this.vmPurchase == null) return

        this.vmPurchase!!.vmManualReceipt.arrayNames.removeAt(indexRow)
        setupRecyclerView()
    }

    override fun didReceiptItemNameChanged(name: String, indexRow: Int) {
        if(this.vmPurchase == null) return

        if(indexRow < this.vmPurchase!!.vmManualReceipt.arrayNames.size) {
            this.vmPurchase!!.vmManualReceipt.arrayNames.set(indexRow, name)
        }
    }

    // MARK Business Logic

    private fun validateFields(): Boolean {
        if(this.vmPurchase == null) return false

        // Amount
        val amount = UtilsString.parseDouble(this.edtAmount.text.toString(), 0.0)
        if(amount < 0.1) {
            showToast("Please enter amount.")
            return false
        }

        // Item names
        if(this.vmPurchase!!.vmManualReceipt.arrayNames.size == 0) {
            showToast("Please add at least one item.")
            return false
        }

        for(name  in this.vmPurchase!!.vmManualReceipt.arrayNames) {
            if(name.isEmpty()) {
                showToast("Please enter items.")
                return false
            }
        }

        this.vmPurchase!!.vmManualReceipt.fAmount = amount
        return true
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
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.next) {
            hideKeyboard()

            if(!validateFields())
                return true

            val cashTransactions: ArrayList<TransactionDetailsViewModel> = this.vmPurchase!!.getTransactionDetailsForCashAccount()
            if (cashTransactions.size > 0) {
                gotoDepositRemainingFragment()
            } else {
                gotoSummaryFragment()
            }
        }
        return true
    }

    private fun gotoDepositRemainingFragment() {
        mainFragment.addFragment(DepositRemainingCashFragment.newInstance(this.vmPurchase))
    }

    private fun gotoSummaryFragment() {
        mainFragment.addFragment(PurchaseSummaryFragment.newInstance(this.vmPurchase))
    }

    companion object {
        @JvmStatic
        fun newInstance(purchase: PurchaseViewModel?) =
            ReceiptDetailsFragment().apply {
                arguments = Bundle().apply {
                    vmPurchase = purchase
                }
            }
    }
}
