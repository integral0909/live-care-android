package com.onseen.livecare.models.Invite.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Organization.DataModel.EnumOrganizationUserRole
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class InviteDataModel: BaseDataModel() {
    var organizationId: String = ""
    var organizationName: String = ""
    var token: String = ""
    var szToEmail: String = ""
    var enumRole: EnumOrganizationUserRole = EnumOrganizationUserRole.CAREGIVER
    var enumStatus: EnumInvitationStatus = EnumInvitationStatus.ACCEPTED

    override fun initialize() {
        super.initialize()

        this.organizationId = ""
        this.organizationName = ""
        this.token = ""
        this.szToEmail = ""
        this.enumRole = EnumOrganizationUserRole.CAREGIVER
        this.enumStatus = EnumInvitationStatus.ACCEPTED
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()
        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "organizationId"))
            this.organizationId = UtilsString.parseString(dictionary["organizationId"])
        if(UtilsBaseFunction.isContainKey(dictionary, "organizationName"))
            this.organizationName = UtilsString.parseString(dictionary["organizationName"])
        if(UtilsBaseFunction.isContainKey(dictionary, "token"))
            this.token = UtilsString.parseString(dictionary["token"])
        if(UtilsBaseFunction.isContainKey(dictionary, "toEmail"))
            this.szToEmail = UtilsString.parseString(dictionary["toEmail"])
        if(UtilsBaseFunction.isContainKey(dictionary, "role"))
            this.enumRole = EnumOrganizationUserRole.fromString(UtilsString.parseString(dictionary["role"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumInvitationStatus.fromString(UtilsString.parseString(dictionary["status"]))
    }
}

enum class EnumInvitationStatus(val value: String) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    DELETED("Deleted");

    companion object {
        fun fromString(status: String): EnumInvitationStatus {
            if(status.isEmpty()) return PENDING

            if(status.toLowerCase().equals(PENDING.value.toLowerCase())) return PENDING
            if(status.toLowerCase().equals(ACCEPTED.value.toLowerCase())) return ACCEPTED
            if(status.toLowerCase().equals(DECLINED.value.toLowerCase())) return DECLINED
            if(status.toLowerCase().equals(DELETED.value.toLowerCase())) return DELETED

            return PENDING
        }
    }
}