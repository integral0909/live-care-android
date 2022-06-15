package com.onseen.livecare.models.Organization.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class OrganizationDataModel: BaseDataModel() {

    var enumStatus: EnumOrganizationStatus = EnumOrganizationStatus.ACTIVE
    var parentId: String = ""
    var szName: String = ""
    var szType: String = ""
    var szPhone: String = ""
    var szEmail: String = ""
    var szHeroUri: String = ""
    var szPhoto: String = ""
    var arrayRegions: ArrayList<String> = ArrayList()

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()

        this.enumStatus = EnumOrganizationStatus.ACTIVE
        this.parentId = ""
        this.szName = ""
        this.szType = ""
        this.szPhone = ""
        this.szEmail = ""
        this.szHeroUri = ""
        this.szPhoto = ""
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "parentId"))
            this.parentId = UtilsString.parseString(dictionary["parentId"])
        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "type"))
            this.szType = UtilsString.parseString(dictionary["type"])
        if(UtilsBaseFunction.isContainKey(dictionary, "phone"))
            this.szPhone = UtilsString.parseString(dictionary["phone"])
        if(UtilsBaseFunction.isContainKey(dictionary, "email"))
            this.szEmail = UtilsString.parseString(dictionary["email"])
        if(UtilsBaseFunction.isContainKey(dictionary, "heroUri"))
            this.szHeroUri = UtilsString.parseString(dictionary["heroUri"])
        if(UtilsBaseFunction.isContainKey(dictionary, "photo"))
            this.szPhoto = UtilsString.parseString(dictionary["photo"])
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumOrganizationStatus.fromString(UtilsString.parseString(dictionary["status"]))
    }
}

enum class EnumOrganizationStatus(val value: String) {
    ACTIVE("Active"),
    DELETED("Deleted");

    companion object {
        fun fromString(status: String?): EnumOrganizationStatus {
            if(status == null || status.equals("")) return ACTIVE

            if(status.toLowerCase().equals(ACTIVE.value.toLowerCase())) return ACTIVE
            if(status.toLowerCase().equals(DELETED.value.toLowerCase())) return DELETED

            return ACTIVE
        }
    }
}