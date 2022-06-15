package com.onseen.livecare.fragments.main.Transactions.ViewModel

import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionStatus
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionType
import com.onseen.livecare.models.Transaction.DataModel.ReceiptDataModel
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class WithdrawalViewModel {
    var date: Date? = null
    private var modelConsumer: ConsumerDataModel? = null
    var modelAccount: FinancialAccountDataModel? = null
    var fAmount: Double = 0.0
    var szDescription: String = ""
    var arrayPhotos: ArrayList<File> = ArrayList()
    var imageConsumerSignature: File? = null
    var imageCaregiverSignature: File? = null
    var isDiscretionarySpending: Boolean = true

    constructor() {
        initialize()
    }

    fun initialize() {
        this.date = null
        this.modelConsumer = null
        this.modelAccount = null
        this.fAmount = 0.0
        this.szDescription = ""
        this.arrayPhotos = ArrayList()
        this.imageConsumerSignature = null
        this.imageCaregiverSignature = null
    }

    fun setModelConsumer(account: ConsumerDataModel?) {
        this.modelConsumer = account
        if(this.modelConsumer != null) {
            this.modelAccount = this.modelConsumer!!.getCashAccount()
        }
    }

    fun getModelConsumer(): ConsumerDataModel? {
        return this.modelConsumer
    }

    fun hasConsumerSigned() : Boolean {
        return this.imageConsumerSignature != null
    }

    fun hasCaregiverSigned() : Boolean {
        return this.imageCaregiverSignature != null
    }

    fun toDataModel(callback: ((transaction: TransactionDataModel?, message: String) -> Unit)) {
        val transaction = TransactionDataModel()

        transaction.szDescription = this.szDescription
        transaction.fAmount = this.fAmount

        transaction.enumType = EnumTransactionType.DEBIT
        transaction.modelConsumer = this.modelConsumer
        transaction.modelAccount = this.modelAccount

        transaction.isDiscretionarySpend = this.isDiscretionarySpending
        if(this.isDiscretionarySpending == true) {
            transaction.enumStatus = EnumTransactionStatus.SUBMITTED
        }
        else {
            // No need to upload photos / signatures
            transaction.enumStatus = EnumTransactionStatus.PENDING
        }

        this.uploadAllPhotos(transaction, callback)
    }

    fun uploadAllPhotos(transaction: TransactionDataModel, callback: ((transaction: TransactionDataModel?, message: String) -> Unit)) {

        this.uploadPhotos(transaction) { successPhotos ->
            if(successPhotos) {
                // No need to upload signatures if not discretionary spending
                if(this.isDiscretionarySpending) {
                    this.uploadConsumerSignature(transaction) { successConsumer ->
                        if(successConsumer) {
                            this.uploadCaregiverSignature(transaction) { successCaregiver ->
                                if(successCaregiver) {
                                    callback(transaction, "")
                                } else {
                                    callback(null, "Upload has failed.")
                                }
                            }
                        } else {
                            callback(null, "Upload has failed.")
                        }
                    }
                } else {
                    callback(transaction, "")
                }
            } else {
                callback(null, "Upload has failed.")
            }
        }
    }

    fun uploadConsumerSignature(transaction: TransactionDataModel, callback: ((success: Boolean) -> Unit)) {
        if(this.modelConsumer == null || this.modelAccount == null) {
            callback(false)
            return
        }
        if(this.imageConsumerSignature == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadPhotoForAccount(this.modelConsumer!!, this.modelAccount!!, this.imageConsumerSignature!!, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    transaction.modelConsumerSignature = responseDataModel.parsedObject as MediaDataModel
                    callback(true)
                }
                else {
                    callback(false)
                }
            }
        })
    }

    fun uploadCaregiverSignature(transaction: TransactionDataModel, callback: ((success: Boolean) -> Unit)) {
        if(this.modelConsumer == null || this.modelAccount == null) {
            callback(false)
            return
        }
        if(this.imageCaregiverSignature == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadPhotoForAccount(this.modelConsumer!!, this.modelAccount!!, this.imageCaregiverSignature!!, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    transaction.modelCaregiverSignature = responseDataModel.parsedObject as MediaDataModel
                    callback(true)
                }
                else {
                    callback(false)
                }
            }
        })
    }

    fun uploadPhotos(transaction: TransactionDataModel, callback: ((success: Boolean) -> Unit)) {
        if(this.modelConsumer == null || this.modelAccount == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadMultiplePhotosForAccount(this.modelConsumer!!, this.modelAccount!!, this.arrayPhotos, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    transaction.arrayReceipts.clear()
                    transaction.arrayReceipts.addAll(ReceiptDataModel.generateReceiptsFromMedia(responseDataModel.parsedObject as ArrayList<MediaDataModel>))

                    callback(true)
                } else {
                    callback(false)
                }
            }
        })
    }
}