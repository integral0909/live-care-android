package com.onseen.livecare.fragments.main.Patients.ViewModel

import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Transaction.DataModel.AuditDataModel
import java.io.File

class AuditViewModel {
    var modelConsumer: ConsumerDataModel? = null
    var modelAccount: FinancialAccountDataModel? = null

    var fAmount: Double = 0.0
    var isApproved: Boolean = false
    var nTries: Int = 0
    var isOverride: Boolean = false
    var imagePhoto: File? = null

    constructor() {
        initialize()
    }

    fun initialize() {
        this.modelConsumer = null
        this.modelAccount = null
        this.fAmount = 0.0
        this.imagePhoto = null
        this.isApproved = false
        this.isOverride = false
        this.nTries = 0
    }

    fun toDataModel(callback: (audit: AuditDataModel?, message: String) -> Unit) {
        val audit = AuditDataModel()
        audit.fBalance = this.fAmount
        audit.isOverride = this.isOverride

        uploadAllPhotos(audit, callback)
    }

    fun uploadAllPhotos(audit: AuditDataModel, callback: (audit: AuditDataModel?, message: String) -> Unit) {
        this.uploadSnapshot(audit){ success ->
            if(success) {
                callback(audit, "")
            } else {
                callback(null, "Upload has failed.")
            }
        }
    }

    fun uploadSnapshot(audit: AuditDataModel, callback: (success: Boolean) -> Unit) {
        if(this.modelConsumer == null || this.modelAccount == null) {
            callback(false)
            return
        }

        if(this.imagePhoto == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadPhotoForAccount(this.modelConsumer!!, this.modelAccount!!, this.imagePhoto!!, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    audit.modelSnapshot = responseDataModel.parsedObject as MediaDataModel
                    callback(true)
                } else {
                    callback(false)
                }
            }
        })
    }

}