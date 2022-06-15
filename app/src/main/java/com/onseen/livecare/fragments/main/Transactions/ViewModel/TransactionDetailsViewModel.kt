package com.onseen.livecare.fragments.main.Transactions.ViewModel

import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.Transaction.DataModel.TransactionDataModel
import com.onseen.livecare.models.Transaction.TransactionManager
import java.io.File

class TransactionDetailsViewModel {

    var modelConsumer: ConsumerDataModel? = null
    private var modelAccount: FinancialAccountDataModel? = null
    var modelPendingTransaction: TransactionDataModel? = null
    var fAmount: Double = 0.0
    var fRemainingDeposit: Double = 0.0
    var imageConsumerSignature: File? = null

    fun initialize() {
        this.modelConsumer = null
        this.modelAccount = null
        this.modelPendingTransaction = null

        this.fAmount = 0.0
        this.fRemainingDeposit = 0.0
        this.imageConsumerSignature = null
    }

    fun setModelAccount(account: FinancialAccountDataModel) {
        this.modelAccount = account
        if(this.modelConsumer != null && this.modelAccount != null) {
            this.modelPendingTransaction = TransactionManager.sharedInstance().getPendingTransactionForAccont(this.modelConsumer!!.id!!, this.modelAccount!!.id!!)
        }
    }

    fun getModelAccount(): FinancialAccountDataModel? {
        return this.modelAccount
    }

    fun getConsumerName(): String {
        if(this.modelConsumer == null) return ""

        return this.modelConsumer!!.szName
    }

    fun getAccountName(): String {
        if(this.modelAccount == null) return ""

        return this.modelAccount!!.szName
    }

    // Returns pending amount of the account which current transaction (self) is tied with.
    fun getPendingAmount() : Double {
        return this.modelPendingTransaction?.fAmount ?: 0.0
    }

    fun hasConsumerSigned() : Boolean {
        return this.imageConsumerSignature != null
    }

    fun hasValidAmount() : Boolean {
        return this.fAmount > 0.01
    }

    fun refreshPendingTransaction() {
        // Re-assigning itself: This will re-detect pending transaction
        setModelAccount(this.modelAccount!!)
    }
}