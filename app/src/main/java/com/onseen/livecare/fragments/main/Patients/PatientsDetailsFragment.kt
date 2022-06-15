package com.onseen.livecare.fragments.main.Patients

import android.os.Bundle

import androidx.recyclerview.widget.RecyclerView
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import applikeysolutions.com.moonrefresh.MoonRefresh
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Transactions.transactions.DepositFragment
import com.onseen.livecare.fragments.main.Transactions.transactions.WithdrawalFragment
import com.onseen.livecare.adapters.ConsumerAccountsAdapter
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Patients.ViewModel.AuditViewModel
import com.onseen.livecare.fragments.main.Patients.ViewModel.FinancialAccountViewModel
import com.onseen.livecare.fragments.main.Transactions.PurchaseFragment
import com.onseen.livecare.fragments.main.Transactions.ViewModel.DepositViewModel
import com.onseen.livecare.fragments.main.Transactions.ViewModel.PurchaseViewModel
import com.onseen.livecare.fragments.main.Transactions.ViewModel.WithdrawalViewModel
import com.onseen.livecare.interfaces.ConsumerAccountButtonListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import kotlinx.android.synthetic.main.fragment_consumer_accounts.*
import de.mrapp.android.bottomsheet.BottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


private const val ARG_CONSUMER = "consumer"

class ConsumerDetailsFragment : BaseMainHomeFragment() {

    private var modelConsumer: ConsumerDataModel? = null
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consumer_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(this.modelConsumer != null) {
            setNavTitle(this.modelConsumer!!.szName, false)
        }

        requestFinancialAccounts()

        fabAdd.setOnClickListener {
            gotoAccountDetailsFragment()
        }
    }

    override fun viewAppeared() {
        reloadData()
    }

    private fun setupRecyclerView() {

        if(this.modelConsumer == null || this.modelConsumer!!.arrayAccounts == null) {
            return
        }

        val consumerAccountsAdapter = ConsumerAccountsAdapter(activity, this.modelConsumer!!.arrayAccounts!!)
        consumerAccountsAdapter.buttonListener = object : ConsumerAccountButtonListener {
            override fun onClickedNewTransaction(account: FinancialAccountDataModel, position: Int) {
                GlobalScope.launch(Dispatchers.Main) {
                    didFinancialAccountNewTransactionClick(account, position)
                }
            }

            override fun onClickedAudit(account: FinancialAccountDataModel, position: Int) {
                GlobalScope.launch(Dispatchers.Main) {
                    didFinancialAccountAuditClick(account)
                }
            }
        }

        viewManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = consumerAccountsAdapter
        }

        pull_to_refresh.setOnRefreshListener(object : MoonRefresh.OnRefreshListener {
            override fun onRefresh() {
                refreshControlDidRefresh()
            }
        })
    }

    private fun refreshControlDidRefresh() {
        if(this.modelConsumer == null) return

        FinancialAccountManager.sharedInstance().requestGetAccountsForConsumer(this.modelConsumer!!, true, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    pull_to_refresh.setRefreshing()
                    if(responseDataModel.isSuccess())
                        reloadData()
                }
            }
        })
    }

    private fun requestFinancialAccounts() {
        if(modelConsumer == null) return

        showProgressHUD()
        FinancialAccountManager.sharedInstance().requestGetAccountsForConsumer(modelConsumer!!, true, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        reloadData()
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun reloadData() {
        setupRecyclerView()
    }

    private fun gotoAccountDetailsFragment() {

        val account = FinancialAccountViewModel()
        account.modelConsumer = this.modelConsumer

        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(AccountDetailsFragment.newInstance(account))
        }
    }

    private fun gotoDepositFragment( account: FinancialAccountDataModel?) {
        if(this.modelConsumer == null || account == null) return

        val deposit: DepositViewModel = DepositViewModel()
        deposit.date = Date()
        deposit.modelConsumer = this.modelConsumer
        deposit.modelAccount = account

        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(DepositFragment.newInstance(deposit))
        }
    }

    private fun gotoPurchaseFragment(account: FinancialAccountDataModel?, index: Int) {

        if(this.modelConsumer == null || this.modelConsumer!!.arrayAccounts == null) return

        if(index >= this.modelConsumer!!.arrayAccounts!!.size) return

        val acc: FinancialAccountDataModel = this.modelConsumer!!.arrayAccounts!![index]
        val purchase: PurchaseViewModel = PurchaseViewModel()

        purchase.arrayTransactionDetails[0].modelConsumer = this.modelConsumer
        purchase.arrayTransactionDetails[0].setModelAccount(acc)

        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(PurchaseFragment.newInstance(purchase))
        }
    }

    private fun gotoWithdrawalFragment(account: FinancialAccountDataModel?) {

        if(this.modelConsumer == null) return

        val withdrawal: WithdrawalViewModel = WithdrawalViewModel()
        withdrawal.date = Date()
        withdrawal.setModelConsumer(this.modelConsumer)

        GlobalScope.launch(Dispatchers.Main) {
            mainFragment.addFragment(WithdrawalFragment.newInstance(withdrawal))
        }
    }

    private fun didFinancialAccountAuditClick(account: FinancialAccountDataModel?) {

        if(this.modelConsumer == null || account == null) return

        val ft = fragmentManager?.beginTransaction() ?: return
        val f = fragmentManager?.findFragmentByTag("AuditDialog")
        if (f != null) {
            ft.remove(f)
        }
        ft.addToBackStack(null)

        val auditDialogFragment = AuditDialogFragment()
        val audit: AuditViewModel = AuditViewModel()
        audit.modelConsumer = this.modelConsumer
        audit.modelAccount = account
        auditDialogFragment.vmAudit = audit
        auditDialogFragment.show(ft, "AuditDialog")
    }

    private fun didFinancialAccountNewTransactionClick(account: FinancialAccountDataModel?, index: Int) {

        if(this.modelConsumer == null || account == null) return

        val builder = BottomSheet.Builder(activity!!)
        builder.setTitle(R.string.new_transaction)
        if(account.enumType == EnumFinancialAccountType.CASH)
            builder.addItem(0, R.string.button_withdrawal)
        builder.addItem(1, R.string.button_deposit)
        builder.addItem(2, R.string.button_purchase)
        builder.setOnItemClickListener { parent, view, position, id ->
            if(id.toInt() == 0) {
                gotoWithdrawalFragment(account)
            } else if(id.toInt() == 1) {
                gotoDepositFragment(account)
            } else if(id.toInt() == 2) {
                gotoPurchaseFragment(account, index)
            }
        }
        val bottomSheet = builder.create()
        bottomSheet.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(consumer: ConsumerDataModel) =
            ConsumerDetailsFragment().apply {
                arguments = Bundle().apply {
                    modelConsumer = consumer
                }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
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
}
