package com.onseen.livecare.models.FinancialAccount.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerRefDataModel
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONArray
import org.json.JSONObject

class FinancialAccountDataModel: BaseDataModel() {

    var szName: String = ""
    var szLast4: String = ""
    var szMerchant: String = ""
    var enumType: EnumFinancialAccountType = EnumFinancialAccountType.CASH
    var isClosed: Boolean = false
    var fBalance: Double = 0.0

    var modelConsumer: ConsumerDataModel? = null
    var refConsumer: ConsumerRefDataModel = ConsumerRefDataModel()
    var arrayRestrictions: ArrayList<RestrictionDataModel> = ArrayList()

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()

        this.szName = ""
        this.szLast4 = ""
        this.szMerchant = ""
        this.modelConsumer = null
        this.enumType = EnumFinancialAccountType.CASH
        this.isClosed = false
        this.fBalance = 0.0
        this.refConsumer = ConsumerRefDataModel()
        this.arrayRestrictions = ArrayList()
    }

    override fun deserialize(dictionary: JSONObject?) {

        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "lastFour"))
            this.szLast4 = UtilsString.parseString(dictionary["lastFour"])
        if(UtilsBaseFunction.isContainKey(dictionary, "marchant"))
            this.szMerchant = UtilsString.parseString(dictionary["marchant"])
        if(UtilsBaseFunction.isContainKey(dictionary, "type"))
            this.enumType = EnumFinancialAccountType.fromString(UtilsString.parseString(dictionary["type"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "closed"))
            this.isClosed = UtilsString.parseBool(dictionary["closed"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "balance"))
            this.fBalance = UtilsString.parseDouble(dictionary["balance"],  0.0)

        if(UtilsBaseFunction.isContainKey(dictionary, "consumer")) {
            val consumer = dictionary.getJSONObject("consumer")
            this.refConsumer.deserialize(consumer)
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "restrictions")) {
            val restrictions: JSONArray = dictionary.getJSONArray("restrictions")
            for(i in 0..restrictions.length()-1) {
                var dict = restrictions.getJSONObject(i)
                val r = RestrictionDataModel()
                r.deserialize(dict)
                if(r.isValid()) {
                    this.arrayRestrictions.add(r)
                }
            }
        }
    }

    fun serializeForCreate(): JSONObject {
        val json: JSONObject = JSONObject()
        json.put("type", this.enumType.value ?: "")
        json.put("name", this.szName ?: "")
        json.put("merchant", this.szMerchant ?: "")
        json.put("lastFour", this.szLast4 ?: "")
        json.put("startingBalance", this.fBalance ?: 0)

        return json
    }
}

enum class EnumFinancialAccountType(val value: String) {

    CASH("Cash"),
    FOOD_STAMP("Food Stamp"),
    GIFT_CARD("Gift Card");

    companion object {
        fun fromString(type: String?): EnumFinancialAccountType {
            if(type == null || type.equals("")) return CASH

            if(type.toLowerCase().equals(CASH.value.toLowerCase())) return CASH
            if(type.toLowerCase().equals(FOOD_STAMP.value.toLowerCase())) return FOOD_STAMP
            if(type.toLowerCase().equals(GIFT_CARD.value.toLowerCase())) return GIFT_CARD

            return CASH
        }
    }
}