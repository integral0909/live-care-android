package com.onseen.livecare.models.FinancialAccount.DataModel

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*

class FinancialAccountRefDataModel {

    var accountId: String = ""
    var enumType: EnumFinancialAccountType = EnumFinancialAccountType.CASH
    var szName: String = ""

    fun initialize() {
        this.accountId = ""
        this.enumType = EnumFinancialAccountType.CASH
        this.szName = ""
    }

    init {
        initialize()
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "accountId"))
            this.accountId = UtilsString.parseString(dictionary["accountId"])
        if(UtilsBaseFunction.isContainKey(dictionary, "type"))
            this.enumType = EnumFinancialAccountType.fromString(UtilsString.parseString(dictionary["type"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
    }
}