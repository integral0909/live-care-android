package com.onseen.livecare.fragments.main.Transactions


import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import com.onseen.livecare.R
import com.onseen.livecare.adapters.PurchaseSummaryAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Transactions.ViewModel.PurchaseViewModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Transaction.TransactionManager
import kotlinx.android.synthetic.main.fragment_purchase_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PurchaseSummaryFragment : BaseMainHomeFragment() {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var purchaseSummaryAdapter: PurchaseSummaryAdapter
    private var vmPurchase: PurchaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavTitle("Summary", false)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        purchaseSummaryAdapter = PurchaseSummaryAdapter(activity!!, this.vmPurchase!!)
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = purchaseSummaryAdapter
        }
    }

    private fun validateFields(): Boolean {
        return true
    }

    private fun preparePurchase() {
        if(!this.validateFields()) return

        showProgressHUD()
        this.vmPurchase!!.toDataModel { transactions, message ->
            GlobalScope.launch(Dispatchers.Main) {
                hideProgressHUD()
                if(transactions != null && transactions.size != 0) {
                    requestPurchase(transactions)
                } else {
                    showToast(message)
                }
            }
        }
    }

    private fun requestPurchase(transactions: ArrayList<TransactionDataModel>) {
        showProgressHUD()
        TransactionManager.sharedInstance().requestMultiplePurchases(transactions, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        gotoTransactionsListFragment()
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun gotoTransactionsListFragment() {
        mainFragment.removeAllFragments()
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
        inflater.inflate(R.menu.submit, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemNext = menu.findItem(R.id.submit)
        itemNext.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            preparePurchase()
            return true
        } else if (item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun newInstance(purchase: PurchaseViewModel?) =
            PurchaseSummaryFragment().apply {
                arguments = Bundle().apply {
                    vmPurchase = purchase
                }
            }
    }
}
