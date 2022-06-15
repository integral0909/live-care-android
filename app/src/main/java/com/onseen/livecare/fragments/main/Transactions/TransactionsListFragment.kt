package com.onseen.livecare.fragments.main.Transactions


import android.animation.Animator
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import applikeysolutions.com.moonrefresh.MoonRefresh

import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Transactions.transactions.DepositFragment
import com.onseen.livecare.fragments.main.Transactions.transactions.WithdrawalFragment
import com.onseen.livecare.adapters.TransactionsSectionAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.interfaces.RowItemClickListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.LocalNotification.LocalNotificationManager
import com.onseen.livecare.models.LocalNotification.LocalNotificationObserver
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Transaction.TransactionManager
import kotlinx.android.synthetic.main.fragment_transactions.*
import kotlinx.android.synthetic.main.fragment_transactions.recyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class TransactionsListFragment : BaseMainHomeFragment(), LocalNotificationObserver {

    private lateinit var viewManager: RecyclerView.LayoutManager
    private var isFABOpened = false
    var arrayTransactionSeactions: ArrayList<TransactionsSectionModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun viewAppeared() {
        super.viewAppeared()
        reloadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavTitle(getString(R.string.tab_transactions), true)

        LocalNotificationManager.sharedInstance().addObserver(this)
        reloadData()
        setupFABs()
    }

    private fun reloadData() {
        val transactions = TransactionManager.sharedInstance().arrayTransactions
        if(transactions.size <= 0) return

        this.arrayTransactionSeactions = generateSections(transactions)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val consumersAdapter = TransactionsSectionAdapter(activity, arrayTransactionSeactions)
        consumersAdapter.itemClickListener = object: RowItemClickListener<TransactionDataModel> {
            override fun onClickedRowItem(obj: TransactionDataModel, position: Int) {
                // position is not for section
            }
        }
        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = consumersAdapter
        }

        pull_to_refresh.setOnRefreshListener(object : MoonRefresh.OnRefreshListener {
            override fun onRefresh() {
                refreshControlDidRefresh()
            }
        })
    }

    private fun refreshControlDidRefresh() {
        TransactionManager.sharedInstance().requestGetTransactions(object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    pull_to_refresh.setRefreshing()
                    if(responseDataModel.isSuccess())
                        reloadData()
                }
            }
        })
    }

    private fun generateSections(transactions: ArrayList<TransactionDataModel>): ArrayList<TransactionsSectionModel> {
        val sortedList = transactions.sortedByDescending { it.dateUpdatedAt }
        val arraySectionModels: ArrayList<TransactionsSectionModel> = ArrayList()

        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = sortedList.get(0).dateUpdatedAt

        var sectionModel = TransactionsSectionModel()
        sectionModel.sectionDate = cal1.time

        for(model in sortedList) {
            val temp = model.dateUpdatedAt
            cal2.time = temp
            if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE)) {
                sectionModel.arrayTransactions.add(model)
            } else {
                arraySectionModels.add(sectionModel)
                cal1.time = temp
                sectionModel = TransactionsSectionModel()
                sectionModel.sectionDate = cal1.time
                sectionModel.arrayTransactions.add(model)
            }
        }

        arraySectionModels.add(sectionModel)
        return arraySectionModels
    }

    private fun setupFABs() {
        fabAdd.setOnClickListener {
            if (isFABOpened)
                closeFABMenu()
            else
                showFABMenu()
        }
        fabBGLayout.setOnClickListener {
            closeFABMenu()
        }
        fabLayout1.setOnClickListener {
            gotoPurchaseFragment()
            closeFABMenu()
        }
        fabLayout2.setOnClickListener {
            gotoDepositFragment()
            closeFABMenu()
        }
        fabLayout3.setOnClickListener {
            gotoWithdrawalFragment()
            closeFABMenu()
        }
    }

    private fun gotoDepositFragment() {
        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(DepositFragment.newInstance(null))
        }
    }

    private fun gotoWithdrawalFragment() {
        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(WithdrawalFragment.newInstance(null))
        }
    }

    private fun gotoPurchaseFragment() {
        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(PurchaseFragment.newInstance(null))
        }
    }

    private fun showFABMenu() {
        isFABOpened = true
        fabLayout1.visibility = View.VISIBLE
        fabLayout2.visibility = View.VISIBLE
        fabLayout3.visibility = View.VISIBLE
        fabBGLayout.visibility = View.VISIBLE
        fabAdd.animate().rotationBy(45.0f)
        fabLayout1.animate().translationY(-resources.getDimension(R.dimen.fab1_distance))
        fabLayout2.animate().translationY(-resources.getDimension(R.dimen.fab2_distance))
        fabLayout3.animate().translationY(-resources.getDimension(R.dimen.fab3_distance))
    }

    private fun closeFABMenu() {
        isFABOpened = false
        fabBGLayout.visibility = View.GONE
        fabAdd.animate().rotation(0.0f)
        fabLayout1.animate().translationY(0.0f)
        fabLayout2.animate().translationY(0.0f)
        fabLayout3.animate().translationY(0.0f)
        fabLayout3.animate().translationY(0.0f).setListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                if (!isFABOpened) {
                    fabLayout1.visibility = View.GONE
                    fabLayout2.visibility = View.GONE
                    fabLayout3.visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TransactionsListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun transactionListUpdated() {
        super.transactionListUpdated()
        reloadData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if(isVisibleToUser) {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
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
        itemNext.isVisible = false
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = false
    }

    override fun onDestroy() {
        LocalNotificationManager.sharedInstance().removeObserver(this)
        super.onDestroy()
    }
}

class TransactionsSectionModel {
    var sectionDate: Date? = null
    var arrayTransactions: ArrayList<TransactionDataModel> = ArrayList()
}
