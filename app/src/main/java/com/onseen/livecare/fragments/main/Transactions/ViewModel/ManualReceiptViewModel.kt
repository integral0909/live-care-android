package com.onseen.livecare.fragments.main.Transactions.ViewModel

import com.onseen.livecare.models.Transaction.DataModel.ReceiptDataModel
import java.util.*
import kotlin.collections.ArrayList

class ManualReceiptViewModel {
    var szVendor: String = ""
    var fAmount: Double = 0.0
    var date: Date? = null
    var arrayNames: ArrayList<String> = arrayListOf("")

    fun initialize() {
        this.fAmount = 0.0
        this.date = null
        this.arrayNames = arrayListOf("")
    }

    fun toDataModel() : ReceiptDataModel {
        val r = ReceiptDataModel()
        r.szVendor = this.szVendor
        r.arrayItems.addAll(this.arrayNames)
        r.date = this.date ?: Date()

        return r
    }
}