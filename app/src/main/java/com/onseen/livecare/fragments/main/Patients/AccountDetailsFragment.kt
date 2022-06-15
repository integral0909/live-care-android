package com.onseen.livecare.fragments.main.Patients

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.BaseMainHomeFragment
import com.onseen.livecare.fragments.main.Patients.ViewModel.FinancialAccountViewModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.android.synthetic.main.fragment_new_account.*
import kotlinx.android.synthetic.main.fragment_new_account.edtAmount
import kotlinx.android.synthetic.main.fragment_new_account.edtDescription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountDetailsFragment : BaseMainHomeFragment() {

    var vmAccount: FinancialAccountViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshFields()
    }

    override fun viewAppeared() {

    }

    private fun refreshFields() {
        if(this.vmAccount == null || this.vmAccount!!.modelConsumer == null) {
            setNavTitle("New Gift Card", false)
            return
        }
        setNavTitle(this.vmAccount!!.modelConsumer!!.szName, false)

        edtMerchant.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                refreshAccountName()
            }
        })

        edtCardNumber.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                refreshAccountName()
            }
        })

        refreshAccountName()
    }

    // Business Login

    private fun refreshAccountName() {

        var merchant = this.edtMerchant.text.toString()
        var card = this.edtCardNumber.text.toString()
        if(merchant.isEmpty() || card.isEmpty()) {
            this.txtAccountName.text = "New Gift Card"
        } else {

            merchant = merchant.take(24)
            card = card.take(4)
            merchant = merchant.capitalize()
            this.txtAccountName.text = merchant + " (" + card + ")"
        }
    }

    private fun validateFields(): Boolean {
        if(this.vmAccount == null) return false

        // Account Name
        this.refreshAccountName()
        val accountName = this.txtAccountName.text.toString()

        val merchant = edtMerchant.text.toString()
        if(merchant.isEmpty()) {
            showToast("Please senter merchant name.")
            return false
        }

        val last4 = this.edtCardNumber.text.toString()
        if(last4.isEmpty()) {
            showToast("Please enter last 4 digits of your gift card.")
            return false
        }

        val amount = UtilsString.parseDouble(edtAmount.text.toString(), -1.0)
        if(amount < 0) {
            showToast("Please enter starting balance.")
            return false
        }

        val desc = edtDescription.text.toString()
        if(desc.isEmpty()) {
            showToast("Please enter description.")
            return false
        }

        this.vmAccount!!.szName = accountName
        this.vmAccount!!.szMerchant = merchant
        this.vmAccount!!.szLast4 = last4
        this.vmAccount!!.fStartingBalance = amount
        this.vmAccount!!.szDescription = desc

        return true
    }

    fun requestCreateCard() {
        if(this.vmAccount!!.modelConsumer == null) {
            showToast("Something went wrong.")
            return
        }

        val account: FinancialAccountDataModel = this.vmAccount!!.toDataModel()
        showProgressHUD()

        FinancialAccountManager.sharedInstance().requestCreateAccount(account, this.vmAccount!!.modelConsumer, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        gotoBack()
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun gotoBack() {
        hideKeyboard()
        mainFragment.onBackPressed()
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
        itemNext.isVisible = false
        val itemSave = menu.findItem(R.id.save)
        itemSave.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.save) {
            hideKeyboard()

            if(validateFields())
                requestCreateCard()

            return true
        } else if (item.itemId == android.R.id.home) {
            mainFragment.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(account: FinancialAccountViewModel?) =
            AccountDetailsFragment().apply {
                arguments = Bundle().apply {
                    vmAccount = account
                }
            }
    }
}
