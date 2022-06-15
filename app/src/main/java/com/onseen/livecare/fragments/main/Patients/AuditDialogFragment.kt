package com.onseen.livecare.fragments.main.Patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.onseen.livecare.R
import com.onseen.livecare.fragments.main.Patients.ViewModel.AuditViewModel
import com.onseen.livecare.interfaces.AuditFragmentListner

class AuditDialogFragment: DialogFragment(), AuditFragmentListner {

    var vmAudit: AuditViewModel? = null
    var isFromDepositRemainingFragment = false
    var strWarningTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        if(isFromDepositRemainingFragment)
            showWarningFragment()
        else
            showAmountFragment()
    }

    override fun onResume() {
        super.onResume()

        val width = resources.getDimensionPixelSize(R.dimen.audit_modal_width)
        val height = resources.getDimensionPixelSize(R.dimen.audit_modal_height)
        dialog?.window?.setLayout(width, height)

    }

    private fun showWarningFragment() {
        val fragment = AuditWarningFragment()
        fragment.listener = this
        fragment.warningText = strWarningTitle
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(0, 0)
        ft.replace(R.id.viewMainContainer, fragment, fragment::class.java.simpleName)
        ft.commit()
    }

    private fun showAmountFragment() {
        val fragment = AuditAmountFragment()
        fragment.listener = this
        fragment.vmAudit = this.vmAudit
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        ft.replace(R.id.viewMainContainer, fragment, fragment::class.java.simpleName)
        ft.commit()
    }

    override fun onPressedCancel() {
        dismiss()
    }

    override fun onPressedNextWithAmount(vmAudit: AuditViewModel?) {
        val fragment = AuditTakePhotoFragment()
        fragment.listener = this
        fragment.vmAudit = vmAudit
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        ft.replace(R.id.viewMainContainer, fragment, fragment::class.java.simpleName)
        ft.commit()
    }

    override fun onPressedNextWithPhoto() {
        val fragment = AuditWarningFragment()
        fragment.listener = this
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        ft.replace(R.id.viewMainContainer, fragment, fragment::class.java.simpleName)
        ft.commit()
    }

    override fun onPressedOKforWarning() {
        if(isFromDepositRemainingFragment) {
            dismiss()
        } else {
            showAmountFragment()
        }
    }
}