package com.onseen.livecare.fragments.main.Patients.ViewModel

import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel

class FinancialAccountViewModel {
    var modelConsumer: ConsumerDataModel? = null

    var szName: String = ""
    var szMerchant: String = ""
    var szLast4: String = ""
    var fStartingBalance: Double = 0.0
    var szDescription: String = ""

    constructor() {
        initialize()
    }

    fun initialize() {
        this.modelConsumer = null
        this.szName = ""
        this.szLast4 = ""
        this.fStartingBalance = 0.0
        this.szDescription = ""
    }

    fun toDataModel() : FinancialAccountDataModel {
        val account = FinancialAccountDataModel()
        account.szName = this.szName
        account.szLast4 = this.szLast4
        account.szMerchant = this.szMerchant
        account.enumType = EnumFinancialAccountType.GIFT_CARD
        account.fBalance = this.fStartingBalance
        account.modelConsumer = this.modelConsumer
        return account
    }
}