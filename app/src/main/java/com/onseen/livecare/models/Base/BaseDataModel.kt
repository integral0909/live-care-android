package com.onseen.livecare.models.Base

import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*

open class BaseDataModel {
    open var id: String? = ""
    open var dateCreatedAt: Date? = null
    open var dateUpdatedAt: Date? = null

    init {
        initialize()
    }

    open fun initialize() {
        id = "";
        dateCreatedAt = null;
        dateUpdatedAt = null;
    }

    open fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "id"))
            this.id = UtilsString.parseString(dictionary["id"])
        if(UtilsBaseFunction.isContainKey(dictionary, "createdAt"))
            this.dateCreatedAt = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["createdAt"]),
            EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
        if(UtilsBaseFunction.isContainKey(dictionary, "updatedAt"))
            this.dateUpdatedAt = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["updatedAt"]),
            EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
    }

    open fun serialize(): JSONObject {
        return JSONObject()
    }

    open fun isValid(): Boolean {
        return (!this.id.equals(""))
    }

}

