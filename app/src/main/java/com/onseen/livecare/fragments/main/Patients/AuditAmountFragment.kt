package com.onseen.livecare.fragments.main.Patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.onseen.livecare.R
import com.onseen.livecare.activities.MainActivity
import com.onseen.livecare.fragments.main.Patients.ViewModel.AuditViewModel
import com.onseen.livecare.interfaces.AuditFragmentListner
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.android.synthetic.main.modal_audit_amount.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuditAmountFragment : Fragment() {

    var listener: AuditFragmentListner? = null

    var vmAudit: AuditViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_audit_amount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(this.vmAudit != null && this.vmAudit!!.modelAccount != null) {
            audio_title.setText("Please count the " + this.vmAudit!!.modelAccount!!.szName + " and enter the amount.")
        }

        btnCancel.setOnClickListener {
            listener?.onPressedCancel()
        }
        btnNext.setOnClickListener {
            (activity as MainActivity).hideKeyboard()
            checkAndGo()
        }
    }

    // business Logic

    fun checkAndGo() {
        if(this.vmAudit == null) return

        val amount = UtilsString.parseDouble(edtAmount.text.toString(), 0.0)
        if(amount < 0.1) {
            (activity as MainActivity).showToast("Please enter valid amount")
            return
        }

        val preAmount = this.vmAudit!!.fAmount
        this.vmAudit!!.fAmount = amount

        if(this.vmAudit!!.nTries == 1 && preAmount == amount) {
            // Already tried with same amount. In this case, we assume caregiver's audit value is correct, and do override
            this.vmAudit!!.isOverride = true
            this.requestAudit()
        } else {
            this.vmAudit!!.nTries = 1
            this.vmAudit!!.isOverride = false
            this.vmAudit!!.imagePhoto = null
            listener?.onPressedNextWithAmount(this.vmAudit)
        }
    }

    fun requestAudit() {
        if(this.vmAudit == null || this.vmAudit!!.modelConsumer == null || this.vmAudit!!.modelAccount == null) {
            Toast.makeText(activity, "Something went wrong.", Toast.LENGTH_LONG).show()
            return
        }

        (activity as MainActivity).showProgressHUD()

        this.vmAudit!!.toDataModel() { modelAudit, message ->
            if(modelAudit != null) {
                FinancialAccountManager.sharedInstance().requestAuditForAccount(modelAudit, this.vmAudit!!.modelConsumer!!, this.vmAudit!!.modelAccount!!, object : NetworkManagerResponse {
                    override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                        GlobalScope.launch(Dispatchers.Main) {
                            (activity as MainActivity).hideProgressHUD()
                            if(responseDataModel.isSuccess()) {
                                listener?.onPressedCancel()
                            } else {
                                (activity as MainActivity).showToast(responseDataModel.getBeautifiedErrorMessage())
                            }
                        }
                    }
                })
            } else {
                (activity as MainActivity).hideProgressHUD()
                (activity as MainActivity).showToast(message)
            }
        }
    }



}
