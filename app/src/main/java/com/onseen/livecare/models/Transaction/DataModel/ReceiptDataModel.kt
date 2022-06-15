package com.onseen.livecare.models.Transaction.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ReceiptDataModel: BaseDataModel() {

    var szVendor: String = ""
    var date: Date? = null
    var modelMedia: MediaDataModel? = MediaDataModel()
    var arrayItems: ArrayList<String> = ArrayList()

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()
        this.szVendor = ""
        this.date = null
        this.modelMedia = MediaDataModel()
        this.arrayItems = ArrayList()
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return
        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "vendor"))
            this.szVendor = UtilsString.parseString(dictionary["vendor"])
        if(UtilsBaseFunction.isContainKey(dictionary, "date"))
            this.date = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["date"]), EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
        if(UtilsBaseFunction.isContainKey(dictionary, "items")) {
            val arrItems = dictionary.getJSONArray("items")
            for(i in 0..arrItems.length()-1) {
                val item: String = UtilsString.parseString(arrItems.get(i))
                this.arrayItems.add(item)
            }
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "media")) {
            val media = dictionary.getJSONObject("media")
            this.modelMedia!!.deserialize(media)
        }
    }

    override fun serialize(): JSONObject {
        val result: JSONObject = JSONObject()

        result.put("vendor", this.szVendor ?: "")
        result.put("date", UtilsDate.getStringFromDateTimeWithFormat(this.date, EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC")))
        val arr = JSONArray()
        for(item in this.arrayItems) {
            arr.put(item)
        }
        result.put("items", arr)

        if(!this.modelMedia?.id.equals("")) {
            result.put("media", this.modelMedia!!.serialize())
        }

        return result
    }

    companion object {
        fun generateReceiptsFromMedia(media: ArrayList<MediaDataModel>?): ArrayList<ReceiptDataModel> {
            if(media == null) return ArrayList()

            val receipts: ArrayList<ReceiptDataModel> = ArrayList()
            for(medium in media) {
                val r = ReceiptDataModel()
                r.modelMedia = medium
                receipts.add(r)
            }

            return receipts
        }
    }
}