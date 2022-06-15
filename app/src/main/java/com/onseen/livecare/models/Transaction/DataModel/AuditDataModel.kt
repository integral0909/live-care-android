package com.onseen.livecare.models.Transaction.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.User.DataModel.UserRefDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*

class AuditDataModel: BaseDataModel() {
    var fBalance: Double = 0.0
    var fDiscrepancy: Double = 0.0
    var dateAudit: Date? = null

    var modelSnapshot: MediaDataModel? = null
    var refUser: UserRefDataModel = UserRefDataModel()
    var enumStatus: EnumAuditStatus = EnumAuditStatus.CLOSED
    var isOverride: Boolean = false

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()
        this.fBalance = 0.0
        this.fDiscrepancy = 0.0
        this.dateAudit = null
        this.modelSnapshot = null
        this.refUser = UserRefDataModel()
        this.enumStatus = EnumAuditStatus.CLOSED
        this.isOverride = false
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "balance"))
            this.fBalance = UtilsString.parseDouble(dictionary["balance"], 0.0)
        if(UtilsBaseFunction.isContainKey(dictionary, "discrepancy"))
            this.fDiscrepancy = UtilsString.parseDouble(dictionary["discrepancy"], 0.0)
        if(UtilsBaseFunction.isContainKey(dictionary, "dateAudit"))
            this.dateAudit = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["dateAudit"]), EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumAuditStatus.fromString(UtilsString.parseString("status"))

        if(UtilsBaseFunction.isContainKey(dictionary, "snapshot")) {
            val snapshot = dictionary.getJSONObject("snapshot")
            this.modelSnapshot = MediaDataModel()
            this.modelSnapshot!!.deserialize(snapshot)
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "user")) {
            val user = dictionary.getJSONObject("user")
            this.refUser.deserialize(user)
        }
    }

    fun serilizeForAudit(): JSONObject {
        val result: JSONObject = JSONObject()
        result.put("balance", this.fBalance)

        if(this.isOverride == true)
            result.put("override", true)
        return result
    }
}

enum class EnumAuditStatus(val value: String) {
    CLOSED("Closed"),
    OPEN("Opend");

    companion object {
        fun fromString(status: String?): EnumAuditStatus {
            if(status == null || status.equals("")) {
                return CLOSED
            }

            if(status.toLowerCase().equals(CLOSED.value.toLowerCase())) {
                return CLOSED
            } else if(status.toLowerCase().equals(OPEN.value.toLowerCase())) {
                return OPEN
            }

            return CLOSED
        }
    }
}