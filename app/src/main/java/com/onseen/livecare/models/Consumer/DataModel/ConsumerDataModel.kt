package com.onseen.livecare.models.Consumer.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.EnumFinancialAccountType
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.Organization.DataModel.EnumOrganizationUserRole
import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class ConsumerDataModel: BaseDataModel() {

    var organizationId: String = ""
    var szOrganizationName: String = ""

    var szExternalKey: String = ""
    var szName: String = ""
    var szNickname: String = ""
    var szRegion: String = ""
    var enumStatus: EnumConsumerStatus = EnumConsumerStatus.ACTIVE


    // Additional Properties
    var arrayAccounts: MutableList<FinancialAccountDataModel>? = null

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()

        this.organizationId = ""
        this.szOrganizationName = ""
        this.szExternalKey = ""
        this.szName = ""
        this.szNickname = ""
        this.szRegion = ""
        this.enumStatus = EnumConsumerStatus.ACTIVE

        this.arrayAccounts = null
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "externalKey"))
            this.szExternalKey = UtilsString.parseString(dictionary["externalKey"])
        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "nickname"))
            this.szNickname = UtilsString.parseString(dictionary["nickname"])
        if(UtilsBaseFunction.isContainKey(dictionary, "region"))
            this.szRegion = UtilsString.parseString(dictionary["region"])
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumConsumerStatus.fromString(UtilsString.parseString(dictionary["status"]))

        if(UtilsBaseFunction.isContainKey(dictionary, "organization")) {
            val org: JSONObject = dictionary.getJSONObject("organization")
            this.organizationId = UtilsString.parseString(org["organizationId"])
            this.szOrganizationName = UtilsString.parseString(org["name"])
        }
    }

    override fun isValid(): Boolean {
        return (!this.id.equals("") && this.enumStatus == EnumConsumerStatus.ACTIVE)
    }

    // MARK - Financial Account Methods

    fun isFinancialAccountLoaded(): Boolean {
        return (this.arrayAccounts != null)
    }

    fun getCashAccount(): FinancialAccountDataModel? {
        if (!isFinancialAccountLoaded()) return null

        return this.arrayAccounts?.firstOrNull { it.enumType == EnumFinancialAccountType.CASH }
    }

    fun setAccountsWithSort(newArray: List<FinancialAccountDataModel>?) {
        this.arrayAccounts = mutableListOf()
        val baseArray = newArray ?: return

        // Sort by: Petty-Cash, Food-Stamp, then Gift-Cards by alphabetical order
        this.arrayAccounts!!.addAll(
            baseArray.filter { it.enumType == EnumFinancialAccountType.CASH }
        )
        this.arrayAccounts!!.addAll(
            baseArray.filter { it.enumType == EnumFinancialAccountType.FOOD_STAMP }
        )
        this.arrayAccounts!!.addAll(
            baseArray
                .filter { it.enumType == EnumFinancialAccountType.GIFT_CARD }
                .sortedBy { it.szName.toLowerCase() }
        )
    }


    // MARK - Search Test Methods

    fun testWithKeyword(keyword: String?): Boolean {
        val queryText = keyword ?: return true
        if (queryText.trim().isEmpty()) return true
        val text = "$szName $szNickname $szExternalKey $szRegion"
        return text.contains(queryText, true)
    }

    fun isValidRegion() : Boolean {
        if (!UserManager.sharedInstance().isLoggedIn()) {
            return false
        }

        val currentUser = UserManager.sharedInstance().currentUser ?: return false

        val role = currentUser.getRoleByOrganizationId(organizationId)
        if (role == EnumOrganizationUserRole.ADMINISTRATOR) {
            return true
        }

        val regions = currentUser.getRegionsByOrganizationId(organizationId)

        return regions.any { it.equals(szRegion, true) }
    }

}

enum class EnumConsumerStatus(val value: String) {
    ACTIVE("Active"),
    DELETED("Deleted");
    companion object {
        fun fromString(status: String?): EnumConsumerStatus {
            if(status == null || status.equals("")) return ACTIVE

            if(status.toLowerCase().equals(ACTIVE.value.toLowerCase())) {
                return ACTIVE
            } else if(status.toLowerCase().equals(DELETED.value.toLowerCase())) {
                return DELETED
            }

            return ACTIVE
        }
    }
}