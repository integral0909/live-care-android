package com.onseen.livecare.models.User.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Base.CoordDataModel
import com.onseen.livecare.models.Organization.DataModel.EnumOrganizationUserRole
import com.onseen.livecare.models.Organization.DataModel.OrganizationRefDataModel
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class UserDataModel: BaseDataModel() {

    var arrayOrganizations: ArrayList<OrganizationRefDataModel> = ArrayList()
    var szName: String = ""
    var szUsername: String = ""
    var szPhone: String = ""
    var szEmail: String = ""
    var szHeroUri: String = ""
    var szPhoto: String = ""
    var szPassword: String = ""

    var isNotifyByEmail: Boolean = false
    var isNotifyByMessage: Boolean = false
    var isNotifyBySMS: Boolean = false

    var szDeviceToken: String = ""

    var modelCoord: CoordDataModel = CoordDataModel()

    var enumStatus: EnumUserStatus = EnumUserStatus.ACTIVE

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()

        this.arrayOrganizations = ArrayList()
        this.szName = ""
        this.szUsername = ""
        this.szPhone = ""
        this.szEmail = ""
        this.szHeroUri = ""
        this.szPhoto = ""
        this.szPassword = ""

        this.isNotifyByEmail = false
        this.isNotifyByMessage = false
        this.isNotifyBySMS = false

        this.szDeviceToken = ""
        this.enumStatus = EnumUserStatus.ACTIVE

        this.modelCoord = CoordDataModel()
    }

    fun serializeForLocalstorage(): JSONObject {
        val dict = JSONObject()

        dict.put("id", this.id)
        dict.put("email", this.szEmail)
        dict.put("password", this.szPassword)

        return dict
    }

    fun deserializeFromLocalstorage(dictionary: JSONObject?) {
        this.initialize()
        if(dictionary == null) return

        this.id = UtilsString.parseString(dictionary["id"])
        this.szEmail = UtilsString.parseString(dictionary["email"])
        this.szPassword = UtilsString.parseString(dictionary["password"])
    }

    override fun deserialize(dictionary: JSONObject?) {

        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "username"))
            this.szUsername = UtilsString.parseString(dictionary["username"])
        if(UtilsBaseFunction.isContainKey(dictionary, "phone"))
            this.szPhone = UtilsString.parseString(dictionary["phone"])
        if(UtilsBaseFunction.isContainKey(dictionary, "email"))
            this.szEmail = UtilsString.parseString(dictionary["email"])
        if(UtilsBaseFunction.isContainKey(dictionary, "heroUri"))
            this.szHeroUri = UtilsString.parseString(dictionary["heroUri"])
        if(UtilsBaseFunction.isContainKey(dictionary, "photo"))
            this.szPhoto = UtilsString.parseString(dictionary["photo"])
        if(UtilsBaseFunction.isContainKey(dictionary, "phone"))
            this.szPhone = UtilsString.parseString(dictionary["phone"])

        if(UtilsBaseFunction.isContainKey(dictionary, "notifyByEmail"))
            this.isNotifyByEmail = UtilsString.parseBool(dictionary["notifyByEmail"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "notifyByMessage"))
            this.isNotifyByMessage = UtilsString.parseBool(dictionary["notifyByMessage"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "notifyBySMS"))
            this.isNotifyBySMS = UtilsString.parseBool(dictionary["notifyBySMS"],  false)

        if(UtilsBaseFunction.isContainKey(dictionary, "deviceToken"))
            this.szDeviceToken = UtilsString.parseString(dictionary["deviceToken"])
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumUserStatus.fromString(UtilsString.parseString(dictionary["status"]))

        if(UtilsBaseFunction.isContainKey(dictionary, "organizations")) {
            val organizations = dictionary.getJSONArray("organizations")
            for(i in 0..organizations.length()-1) {
                val dict = organizations.getJSONObject(i)
                val org = OrganizationRefDataModel()
                org.deserialize(dict)
                if(org.isValid()) this.arrayOrganizations.add(org)
            }
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "location")) {
            val coord = dictionary.getJSONObject("location")
            this.modelCoord.deserialize(coord)
        }
    }

    override fun isValid(): Boolean {
        return (!this.id.equals("") && this.enumStatus == EnumUserStatus.ACTIVE)
    }

    fun getRegionsByOrganizationId(organizationId: String) : List<String> {
        return arrayOrganizations.firstOrNull { it.organizationId == organizationId }?.arrayRegions ?: listOf()
    }

    fun getRoleByOrganizationId(organizationId: String) : EnumOrganizationUserRole {
        return arrayOrganizations.firstOrNull { it.organizationId == organizationId }?.enumRole ?: EnumOrganizationUserRole.CAREGIVER
    }

}

enum class EnumUserStatus(val value: String) {
    ACTIVE("Active"),
    DELETED("Deleted");

    companion object{
        fun fromString(status: String?): EnumUserStatus {
            if(status == null || status.equals("")) return ACTIVE

            if(status.toLowerCase().equals(ACTIVE.value.toLowerCase())) return ACTIVE
            if(status.toLowerCase().equals(DELETED.value.toLowerCase())) return DELETED

            return ACTIVE
        }
    }
}