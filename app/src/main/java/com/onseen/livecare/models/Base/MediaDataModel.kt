package com.onseen.livecare.models.Base

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class MediaDataModel {
    var id: String = ""
    var enumType: EnumMediaMimeType = EnumMediaMimeType.PNG
    var szFilename: String = ""
    var szEncoding: String = ""
    var nSize: Int = 0

    init {
        initialize()
    }

    fun initialize() {
        this.id = ""
        this.enumType = EnumMediaMimeType.PNG
        this.szFilename = ""
        this.szEncoding = ""
        this.nSize = 0
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "id")) {
            this.id = UtilsString.parseString(dictionary["id"])
        } else if(UtilsBaseFunction.isContainKey(dictionary, "mediaId")) {
            this.id = UtilsString.parseString(dictionary["mediaId"])
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "mimeType"))
            this.enumType = EnumMediaMimeType.fromString(UtilsString.parseString(dictionary["mimeType"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "fileName"))
            this.szFilename = UtilsString.parseString(dictionary["fileName"])
        if(UtilsBaseFunction.isContainKey(dictionary, "encoding"))
            this.szEncoding = UtilsString.parseString(dictionary["encoding"])
        if(UtilsBaseFunction.isContainKey(dictionary, "size"))
            this.nSize = UtilsString.parseInt(dictionary["size"], 0)
    }

    fun serialize(): JSONObject {
        val json = JSONObject()
        json.put("mediaId", this.id)
        json.put("mimeType", this.enumType.value)
        json.put("fileName", this.szFilename)
        json.put("encoding", this.szEncoding)
        json.put("size", this.nSize)
        return json
    }
}

enum class EnumMediaMimeType(val value: String) {
    PNG("image/png"),
    JPEG("image/jpg"),
    PDF("application/pdf");

    companion object {
        fun fromString(string: String?): EnumMediaMimeType {
            if(string == null || string.equals("")) return PNG

            if(string.toLowerCase().equals(PNG.value.toLowerCase()))
                return PNG
            else if(string.toLowerCase().equals(JPEG.value.toLowerCase()))
                return JPEG
            else if(string.toLowerCase().equals(PDF.value.toLowerCase()))
                return PDF

            return PNG
        }
    }
}