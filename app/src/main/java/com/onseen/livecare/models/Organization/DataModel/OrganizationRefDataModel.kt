package com.onseen.livecare.models.Organization.DataModel

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class OrganizationRefDataModel {
    var organizationId: String = ""
    var szName: String = ""
    var szPhoto: String = ""
    var szPhone: String = ""
    var nMaxTimeOnVehice: Int = 0
    var nMaxLimitBuffer: Int = 0
    var nMaxMileage: Int = 0

    var arrayRegions: MutableList<String> = mutableListOf()

    var enumStatus: EnumOrganizationStatus = EnumOrganizationStatus.ACTIVE
    var enumRole: EnumOrganizationUserRole = EnumOrganizationUserRole.CAREGIVER

    init {
        initialize()
    }

    fun initialize() {
        this.organizationId = ""
        this.szName = ""
        this.enumStatus = EnumOrganizationStatus.ACTIVE
        this.enumRole = EnumOrganizationUserRole.CAREGIVER
        this.arrayRegions = mutableListOf()
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "organizationId"))
            this.organizationId = UtilsString.parseString(dictionary["organizationId"])
        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "photo"))
            this.szPhoto = UtilsString.parseString(dictionary["photo"])
        if(UtilsBaseFunction.isContainKey(dictionary, "phone"))
            this.szPhone = UtilsString.parseString(dictionary["phone"])
        if(UtilsBaseFunction.isContainKey(dictionary, "maxTimeOnVehicle"))
            this.nMaxTimeOnVehice = UtilsString.parseInt(dictionary["maxTimeOnVehicle"], 0)
        if(UtilsBaseFunction.isContainKey(dictionary, "maxLimitBuffer"))
            this.nMaxLimitBuffer = UtilsString.parseInt(dictionary["maxLimitBuffer"], 0)
        if(UtilsBaseFunction.isContainKey(dictionary, "maxMileage"))
            this.nMaxMileage = UtilsString.parseInt(dictionary["maxMileage"], 0)
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumOrganizationStatus.fromString(UtilsString.parseString(dictionary["status"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "role"))
            this.enumRole = EnumOrganizationUserRole.fromString(UtilsString.parseString(dictionary["role"]))

        if(UtilsBaseFunction.isContainKey(dictionary, "regions")) {
            val regions = dictionary.getJSONArray("regions")
            for(i in 0 until regions.length()) {
                val region = UtilsString.parseString(regions.get(i))
                this.arrayRegions.add(region)
            }
        }
    }

    fun isValid(): Boolean {
        return (this.enumStatus == EnumOrganizationStatus.ACTIVE) && (this.enumRole == EnumOrganizationUserRole.CAREGIVER || this.enumRole == EnumOrganizationUserRole.ADMINISTRATOR)
    }
}

enum class EnumOrganizationUserRole(val value: String) {
    ADMINISTRATOR("Administrator"),
    CAREGIVER("User"),
    DRIVER("Driver"),
    PM("PM"),
    GUARDIAN("Guardian"),
    DISPATCH("Dispatch");

    companion object {
        fun fromString(role: String?): EnumOrganizationUserRole {
            if(role == null || role.equals("")) return CAREGIVER

            if(role.toLowerCase().equals(ADMINISTRATOR.value.toLowerCase())) {
                return ADMINISTRATOR
            }
            if(role.toLowerCase().equals(CAREGIVER.value.toLowerCase())) {
                return CAREGIVER
            }
            if(role.toLowerCase().equals(DRIVER.value.toLowerCase())) {
                return DRIVER
            }
            if(role.toLowerCase().equals(PM.value.toLowerCase())) {
                return PM
            }
            if(role.toLowerCase().equals(GUARDIAN.value.toLowerCase())) {
                return GUARDIAN
            }
            if(role.toLowerCase().equals(DISPATCH.value.toLowerCase())) {
                return DISPATCH
            }

            return CAREGIVER
        }
    }
}