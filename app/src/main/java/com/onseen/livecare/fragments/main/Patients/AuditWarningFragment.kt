package com.onseen.livecare.fragments.main.Patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onseen.livecare.R
import com.onseen.livecare.interfaces.AuditFragmentListner
import kotlinx.android.synthetic.main.modal_audit_warning.*

class AuditWarningFragment : Fragment() {

    var listener: AuditFragmentListner? = null
    var warningText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_audit_warning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!warningText.isEmpty()) {
            warning_descriptoin.setText(warningText)
        }
        btnOK.setOnClickListener {
            listener?.onPressedOKforWarning()
        }
    }

}
