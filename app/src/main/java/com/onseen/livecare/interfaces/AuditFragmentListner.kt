package com.onseen.livecare.interfaces

import com.onseen.livecare.fragments.main.Patients.ViewModel.AuditViewModel

interface AuditFragmentListner {
    fun onPressedCancel()
    fun onPressedNextWithAmount(vmAudit: AuditViewModel?)
    fun onPressedNextWithPhoto()
    fun onPressedOKforWarning()
}