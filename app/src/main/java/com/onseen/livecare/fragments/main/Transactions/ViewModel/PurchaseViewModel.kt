package com.onseen.livecare.fragments.main.Transactions.ViewModel

import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.FinancialAccount.FinancialAccountManager
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionType
import com.onseen.livecare.models.Transaction.DataModel.ReceiptDataModel
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Utils.DispatchGroupManager
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class PurchaseViewModel {
    var date: Date? = Date()
    var szMerchant: String = ""
    var szDescription: String = ""
    var hasReceiptPhotos: Boolean = true
    var hasRemainingDeposit: Boolean = true

    var arrayTransactionDetails: ArrayList<TransactionDetailsViewModel> = arrayListOf(TransactionDetailsViewModel())
    var vmManualReceipt: ManualReceiptViewModel = ManualReceiptViewModel()
    var arrayReceiptPhotos: ArrayList<File> = ArrayList()
    var imageCaregiverSignature: File? = null

    fun initialize() {
        this.date = null
        this.szMerchant = ""
        this.szDescription = ""
        this.hasReceiptPhotos = true
        this.hasRemainingDeposit = true
        this.arrayTransactionDetails = arrayListOf(TransactionDetailsViewModel())
        this.vmManualReceipt = ManualReceiptViewModel()
        this.arrayReceiptPhotos = ArrayList()
        this.imageCaregiverSignature = null
    }

    fun getTotalAmount() : Double {
        var total: Double = 0.0
        for(transaction in this.arrayTransactionDetails) {
            total = total + transaction.fAmount
        }
        return total
    }

    fun hasCaregiverSigned() : Boolean {
        return this.imageCaregiverSignature != null
    }

    fun hasConsumersSigned(): Boolean {
        for(transaction in this.arrayTransactionDetails) {
            if(!transaction.hasConsumerSigned()) {
                return false
            }
        }
        return true
    }

    fun getTransactionDetailsForCashAccount(): ArrayList<TransactionDetailsViewModel> {
        val array: ArrayList<TransactionDetailsViewModel> = ArrayList()
        for(transaction in this.arrayTransactionDetails) {
            if(transaction.getModelAccount() != null) {
                if(transaction.getModelAccount()!!.enumType == EnumFinancialAccountType.CASH) {
                    array.add(transaction)
                }
            }
        }

        return array
    }

    fun toDataModel(callback: ((transactions: ArrayList<TransactionDataModel>?, message: String) -> Unit)) {
        val transactions: ArrayList<TransactionDataModel> = ArrayList()
        val transactionsGroup = DispatchGroupManager()

        for(t in this.arrayTransactionDetails) {
            val transaction = TransactionDataModel()

            transaction.id = if(t.modelPendingTransaction?.id?.isEmpty() == false) t.modelPendingTransaction?.id else "" // ???
            transaction.szDescription = this.szDescription
            transaction.fAmount = t.fAmount
            transaction.isDiscretionarySpend = false
            transaction.enumType = EnumTransactionType.DEBIT

            transaction.modelConsumer = t.modelConsumer
            transaction.modelAccount = t.getModelAccount()
            transaction.hasDepositRemaining = this.hasRemainingDeposit

            transactionsGroup.enter()
            this.uploadAllPhotosForTransaction(transaction, t.imageConsumerSignature) { updatedTransaction, message ->
                if (updatedTransaction != null) {
                    if(!this.hasReceiptPhotos)
                        updatedTransaction.arrayReceipts.add(this.vmManualReceipt.toDataModel())

                    transactions.add(updatedTransaction)
                }
                transactionsGroup.leave()
            }
        }

        transactionsGroup.notify(Runnable {
            if (transactions.size == this.arrayTransactionDetails.size) {
                callback(transactions, "")
            }
            else {
                callback(null, "Upload has failed.")
            }
        })
    }

    fun uploadAllPhotosForTransaction(transaction: TransactionDataModel, imageConsumerSignature: File?, callback: ((transaction: TransactionDataModel?, message: String) -> Unit)) {

        this.uploadPhotos(transaction) { successPhotos ->
            if(successPhotos) {
                callback(transaction, "")
            } else {
                callback(null, "Upload has failed.")
            }
        }

//        this.uploadConsumerSignature(transaction, imageConsumerSignature) { successConsumer ->
//            if(successConsumer) {
//                this.uploadCaregiverSignature(transaction) { successCaregiver ->
//                    if(successCaregiver) {
//                        this.uploadPhotos(transaction) { successPhotos ->
//                            if(successPhotos) {
//                                callback(transaction, "")
//                            } else {
//                                callback(null, "Upload has failed.")
//                            }
//                        }
//                    } else {
//                        callback(null, "Upload has failed.")
//                    }
//                }
//            } else {
//                callback(null, "Upload has failed.")
//            }
//        }
    }

    fun uploadConsumerSignature(transaction: TransactionDataModel, imageConsumerSignature: File?, callback: ((success: Boolean) -> Unit)) {
        if(transaction.modelConsumer == null && transaction.modelAccount == null) {
            callback(false)
            return
        }
        if(imageConsumerSignature == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadPhotoForAccount(transaction.modelConsumer!!, transaction.modelAccount!!, imageConsumerSignature, object : NetworkManagerResponse{
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
        if(transaction.modelConsumer == null && transaction.modelAccount == null) {
            callback(false)
            return
        }
        if(this.imageCaregiverSignature == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadPhotoForAccount(transaction.modelConsumer!!, transaction.modelAccount!!, this.imageCaregiverSignature!!, object : NetworkManagerResponse{
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
        if(transaction.modelConsumer == null || transaction.modelAccount == null) {
            callback(false)
            return
        }

        FinancialAccountManager.sharedInstance().requestUploadMultiplePhotosForAccount(transaction.modelConsumer!!, transaction.modelAccount!!, this.arrayReceiptPhotos, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    transaction.arrayReceipts.clear()
                    transaction.arrayReceipts.addAll(ReceiptDataModel.generateReceiptsFromMedia((responseDataModel.parsedObject as ArrayList<MediaDataModel>)))
                    callback(true)
                }
                else {
                    callback(false)
                }
            }
        })
    }
}