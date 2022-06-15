package com.onseen.livecare.interfaces

import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel

interface ConsumerAccountButtonListener {
    fun onClickedNewTransaction(account: FinancialAccountDataModel, position: Int)
    fun onClickedAudit(account: FinancialAccountDataModel, position: Int)
}