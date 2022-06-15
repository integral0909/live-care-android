package com.onseen.livecare.interfaces

interface ReceiptItemsListener {
    fun didReceiptItemDeleteClick(indexRow: Int)
    fun didReceiptItemNameChanged(name: String, indexRow: Int)
}