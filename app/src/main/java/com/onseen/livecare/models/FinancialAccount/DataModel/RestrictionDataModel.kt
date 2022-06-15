package com.onseen.livecare.models.FinancialAccount.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*

class RestrictionDataModel: BaseDataModel() {

    var fMaxSpend: Double = 0.0
    var fDiscretionarySpend: Double = 0.0
    var enumSpendPeriod: EnumDiscretionarySpendPeriod = EnumDiscretionarySpendPeriod.DAILY
    var dateEffective: Date? = null
    var dateExpiration: Date? = null

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()

        this.fMaxSpend = 0.0
        this.fDiscretionarySpend = 0.0
        this.enumSpendPeriod = EnumDiscretionarySpendPeriod.DAILY
        this.dateEffective = null
        this.dateExpiration = null
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "maxSpend"))
            this.fMaxSpend = UtilsString.parseDouble(dictionary["maxSpend"], 0.0)
        if(UtilsBaseFunction.isContainKey(dictionary, "discretionarySpend"))
            this.fDiscretionarySpend = UtilsString.parseDouble(dictionary["discretionarySpend"], 0.0)
        if(UtilsBaseFunction.isContainKey(dictionary, "discretionarySpendPeriod"))
            this.enumSpendPeriod = EnumDiscretionarySpendPeriod.fromString(UtilsString.parseString(dictionary["discretionarySpendPeriod"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "effectiveDate"))
            this.dateEffective = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["effectiveDate"]), EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value,  TimeZone.getTimeZone("UTC"))
        if(UtilsBaseFunction.isContainKey(dictionary, "expirationDate"))
            this.dateExpiration = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["expirationDate"]), EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
    }

}

enum class EnumDiscretionarySpendPeriod(val value: String) {
    WEEKLY("Weekly"),
    DAILY("Daily");

    companion object {
        fun fromString(period: String?): EnumDiscretionarySpendPeriod {
            if(period == null || period.equals("")) return DAILY

            if(period.toLowerCase().equals(DAILY.toString().toLowerCase())) return DAILY
            if(period.toLowerCase().equals(WEEKLY.toString().toLowerCase())) return WEEKLY

            return DAILY
        }
    }
}