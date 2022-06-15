package com.onseen.livecare.interfaces

interface PurchaseReceiptsListener {
    fun didTransactionDetailsConsumerSelected(indexConsumer: Int, indexRow: Int)
    fun didTransactionDetailsAccountSelected(indexAccount: Int, indexRow: Int)
    fun didTransactionDetailsAmountChanged( amount: Double, indexRow: Int)
    fun didTransactionDetailsDeleteClick(indexRow: Int)
}