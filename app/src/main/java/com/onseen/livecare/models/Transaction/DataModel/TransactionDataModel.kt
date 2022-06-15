package com.onseen.livecare.models.Transaction.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerRefDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountRefDataModel
import com.onseen.livecare.models.User.DataModel.UserDataModel
import com.onseen.livecare.models.User.DataModel.UserRefDataModel
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONArray
import org.json.JSONObject

class TransactionDataModel: BaseDataModel() {

    var szDescription: String = ""
    var fAmount: Double = 0.0
    var fTotal: Double = 0.0
    var isExceedsMaxSpendForPeriod: Boolean = false
    var isDiscretionarySpend: Boolean = false
    var hasDepositRemaining: Boolean = false

    var enumType: EnumTransactionType = EnumTransactionType.DEBIT
    var enumStatus: EnumTransactionStatus = EnumTransactionStatus.PENDING

    var refAccount: FinancialAccountRefDataModel = FinancialAccountRefDataModel()
    var refConsumer: ConsumerRefDataModel = ConsumerRefDataModel()
    var refUser: UserRefDataModel = UserRefDataModel()

    var modelAccount: FinancialAccountDataModel? = null
    var modelConsumer: ConsumerDataModel? = null
    var modelUser: UserDataModel? = null

    var modelConsumerSignature: MediaDataModel? = null
    var modelCaregiverSignature: MediaDataModel? = null
    var arrayReceipts: ArrayList<ReceiptDataModel> = ArrayList()

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()
        this.szDescription = ""
        this.fAmount = 0.0
        this.fTotal = 0.0
        this.isExceedsMaxSpendForPeriod = false
        this.isDiscretionarySpend = false
        this.hasDepositRemaining = false

        this.enumType = EnumTransactionType.DEBIT
        this.enumStatus = EnumTransactionStatus.PENDING

        this.refAccount = FinancialAccountRefDataModel()
        this.refConsumer = ConsumerRefDataModel()
        this.refUser = UserRefDataModel()

        modelAccount = null
        modelConsumer = null
        modelUser = null

        modelConsumerSignature = null
        modelCaregiverSignature = null

        this.arrayReceipts = ArrayList()
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "description"))
            this.szDescription = UtilsString.parseString(dictionary["description"])
        if(UtilsBaseFunction.isContainKey(dictionary, "amount"))
            this.fAmount = UtilsString.parseDouble(dictionary["amount"], 0.0)
        if(UtilsBaseFunction.isContainKey(dictionary, "total"))
            this.fTotal = UtilsString.parseDouble(dictionary["total"], 0.0)
        if(UtilsBaseFunction.isContainKey(dictionary, "exceedsMaxSpendForPeriod"))
            this.isExceedsMaxSpendForPeriod = UtilsString.parseBool(dictionary["exceedsMaxSpendForPeriod"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "discretionarySpend"))
            this.isDiscretionarySpend = UtilsString.parseBool(dictionary["discretionarySpend"], false)

        if(UtilsBaseFunction.isContainKey(dictionary, "type"))
            this.enumType = EnumTransactionType.fromString(UtilsString.parseString(dictionary["type"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumTransactionStatus.fromString(UtilsString.parseString(dictionary["status"]))

        if(UtilsBaseFunction.isContainKey(dictionary, "account")) {
            val account = dictionary.getJSONObject("account")
            this.refAccount.deserialize(account)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "consumer")) {
            val consumer = dictionary.getJSONObject("consumer")
            this.refConsumer.deserialize(consumer)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "user")) {
            val user = dictionary.getJSONObject("user")
            this.refUser.deserialize(user)
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "receipts")) {
            val receipts = dictionary.getJSONArray("receipts")
            for(i in 0..receipts.length()-1) {
                val dict = receipts.getJSONObject(i)
                val r = ReceiptDataModel()
                r.deserialize(dict)
                if(r.isValid()) {
                    this.arrayReceipts.add(r)
                }
            }
        }
    }

    fun serializeForDeposit(): JSONObject {
        val result = JSONObject()
        result.put("amount", this.fAmount)
        result.put("description", this.szDescription)
        result.put("status", this.enumStatus.value)
        result.put("type", this.enumType.value)

        /*
        if(modelConsumerSignature != null) {
            result.put("consumerSignature", modelConsumerSignature!!.serialize())
        }

        if(modelCaregiverSignature != null) {
            result.put("userSignature", modelCaregiverSignature!!.serialize())
        }*/

        val receipts: JSONArray = JSONArray()
        for(i in 0.. arrayReceipts.size-1) {
            val r = arrayReceipts.get(i)
            receipts.put(r.serialize())
        }

        result.put("receipts", receipts)
        return result
    }

    fun serializeForWithdrawal(): JSONObject {
        val result = JSONObject()

        result.put("amount", this.fAmount)
        result.put("description", this.szDescription)
        result.put("status", this.enumStatus.value)
        result.put("type", this.enumType.value)
        result.put("discretionarySpend", this.isDiscretionarySpend)

        if(this.modelConsumerSignature != null) {
            result.put("consumerSignature", this.modelConsumerSignature!!.serialize())
        }

        if(this.modelCaregiverSignature != null) {
            result.put("userSignature", this.modelCaregiverSignature!!.serialize())
        }

        val receipts = JSONArray()
        for (r in arrayReceipts)
            receipts.put(r.serialize())

        result.put("receipts", receipts)
        return result
    }

    fun serializeForPurchase(): JSONObject {
        val result = JSONObject()
        result.put("amount", this.fAmount ?: 0)
        result.put("description", this.szDescription ?: "")
        result.put("type", this.enumType.value)
        result.put("depositRemaining", this.hasDepositRemaining)

        if(this.modelConsumerSignature != null) {
            result.put("consumerSignature", this.modelConsumerSignature!!.serialize())
        }
        if(this.modelCaregiverSignature != null) {
            result.put("userSignature", this.modelCaregiverSignature!!.serialize())
        }

        val receipts: JSONArray = JSONArray()
        for(i in 0..this.arrayReceipts.size-1) {
            val r = arrayReceipts.get(i)
            receipts.put(r.serialize())
        }

        result.put("receipts", receipts)
        return result
    }
}

enum class EnumTransactionType(val value: String) {
    CREDIT("Credit"),
    DEBIT("Debit");

    companion object {
        fun fromString(type: String?): EnumTransactionType {
            if(type == null || type == "") return DEBIT

            if(type.toLowerCase() == CREDIT.value.toLowerCase()) return CREDIT
            if(type.toLowerCase() == DEBIT.value.toLowerCase()) return  DEBIT

            return DEBIT
        }
    }

    fun getBeautifiedText(): String {
        if(this == CREDIT) return "Deposit"
        if(this == DEBIT) return "Withdraw"

        return ""
    }
}

enum class EnumTransactionStatus(val value: String) {
    PENDING("Pending"),
    SUBMITTED("Submitted"),
    APPROVED("Approved");

    companion object{
        fun fromString(status: String?) : EnumTransactionStatus {
            if(status == null || status == "") return PENDING

            if(status.toLowerCase() == PENDING.value.toLowerCase()) return PENDING
            if(status.toLowerCase() == SUBMITTED.value.toLowerCase()) return SUBMITTED
            if(status.toLowerCase() == APPROVED.value.toLowerCase()) return APPROVED

            return PENDING
        }
    }
}