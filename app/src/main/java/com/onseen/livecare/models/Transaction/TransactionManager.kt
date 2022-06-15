package com.onseen.livecare.models.Transaction

import com.onseen.livecare.models.Communication.*
import com.onseen.livecare.models.LocalNotification.LocalNotificationManager
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionStatus
import com.onseen.livecare.models.Transaction.DataModel.EnumTransactionType
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Utils.DispatchGroupManager
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.TRANSACTIONS_LISTUPDATED
import org.json.JSONArray
import org.json.JSONObject

class TransactionManager {

    companion object {
        private val sharedInstance: TransactionManager = TransactionManager()

        @Synchronized
        fun sharedInstance(): TransactionManager {
            return sharedInstance
        }
    }

    var arrayTransactions: ArrayList<TransactionDataModel> = ArrayList()

    init {
        initialize()
    }

    fun initialize() {
        this.arrayTransactions = ArrayList()
    }

    fun addTransactionIfNeede(newTransaction: TransactionDataModel) {
        if(newTransaction.isValid()) return

        synchronized(this.arrayTransactions) {
            for(transaction in this.arrayTransactions) {
                if(transaction.id.equals(newTransaction.id)) return
            }
            this.arrayTransactions.add(newTransaction)
        }
    }

    fun getPendingTransactionForAccont(consumerId: String, accountId: String): TransactionDataModel? {
        synchronized(this.arrayTransactions) {
            for(transaction in this.arrayTransactions) {
                if (transaction.refConsumer.consumerId == consumerId && transaction.refAccount.accountId == accountId &&
                    transaction.enumStatus == EnumTransactionStatus.PENDING && transaction.enumType == EnumTransactionType.DEBIT) {
                    return transaction
                }
            }
        }

        return null
    }

    // MARK: API calls
    fun requestGetTransactions(callback: NetworkManagerResponse?) {
        val urlString = UrlManager.TransactionApi.getTransactions()
        NetworkManager.GET(urlString, null, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess() && responseDataModel.payload.has("data") && !responseDataModel.payload.isNull("data")) {
                    val array: ArrayList<TransactionDataModel> = ArrayList()
                    val data: JSONArray = responseDataModel.payload.getJSONArray("data")
                    for(i in 0..data.length()-1) {
                        val dict = data.getJSONObject(i)
                        val transaction = TransactionDataModel()
                        transaction.deserialize(dict)
                        if(transaction.isValid()) {
                            array.add(transaction)
                        }
                    }
                    arrayTransactions.clear()
                    arrayTransactions.addAll(array)
                }

                LocalNotificationManager.sharedInstance().notifyLocalNotification(TRANSACTIONS_LISTUPDATED)

                callback?.onComplete(responseDataModel)
            }
        })
    }

    fun requestDeposit(deposit: TransactionDataModel, callback: NetworkManagerResponse) {
        if(deposit.modelConsumer == null || deposit.modelAccount == null) {
            callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            return
        }

        val urlString = UrlManager.TransactionApi.createTransaction(deposit.modelConsumer!!.organizationId, deposit.modelConsumer!!.id!!, deposit.modelAccount!!.id!!)

        NetworkManager.POST(urlString, deposit.serializeForDeposit(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    requestGetTransactions(callback)
                } else {
                    callback.onComplete(responseDataModel)
                }
            }
        })
    }

    fun requestWithdrawal(withdrawal: TransactionDataModel, callback: NetworkManagerResponse) {

        val consumer = withdrawal.modelConsumer
        val account = withdrawal.modelAccount
        if(consumer == null || account == null) {
            callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            return
        }

        val urlString = UrlManager.TransactionApi.createTransaction(consumer.organizationId, consumer.id!!, account.id!!)
        NetworkManager.POST(urlString, withdrawal.serializeForWithdrawal(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    val newTransaction = TransactionDataModel()
                    newTransaction.deserialize(responseDataModel.payload)
                    responseDataModel.parsedObject = newTransaction
                }
                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestMultiplePurchases(purchases: ArrayList<TransactionDataModel>, callback: NetworkManagerResponse) {
        val purchasesGroup = DispatchGroupManager()
        var allSucceeded: Boolean = true

        for(purchase in purchases) {
            purchasesGroup.enter()

            this.requestPurchase(purchase, object : NetworkManagerResponse {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    if(responseDataModel.isSuccess() == false) {
                        allSucceeded = false
                    }
                    purchasesGroup.leave()
                }
            })
        }

        purchasesGroup.notify(Runnable {
            if (allSucceeded == true) {
                callback.onComplete(NetworkResponseDataModel.instanceForSuccess())
            }
            else {
                callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            }
        })
    }

    fun requestPurchase(purchase: TransactionDataModel?, callback: NetworkManagerResponse) {

        if(purchase!!.modelConsumer == null || purchase.modelAccount == null) {
            callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            return
        }

        if(!purchase.id!!.isEmpty()) {
            val urlString: String = UrlManager.TransactionApi.updateTransaction(purchase.modelConsumer!!.organizationId, purchase.modelConsumer!!.id!!, purchase.modelAccount!!.id!!, purchase.id!!)
            NetworkManager.PUT(urlString, purchase.serializeForPurchase(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    if (responseDataModel.isSuccess()) {
                        requestGetTransactions(callback)
                    }
                    else {
                        callback.onComplete(responseDataModel)
                    }
                }
            })
        } else {
            val urlString: String = UrlManager.TransactionApi.createTransaction(purchase.modelConsumer!!.organizationId, purchase.modelConsumer!!.id!!, purchase.modelAccount!!.id!!)
            NetworkManager.POST(urlString, purchase.serializeForPurchase(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    if (responseDataModel.isSuccess()) {
                        requestGetTransactions(callback)
                    }
                    else {
                        callback.onComplete(responseDataModel)
                    }
                }
            })
        }
    }
}