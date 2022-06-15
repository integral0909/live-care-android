package com.onseen.livecare.models.Consumer.DataModel

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class ConsumerRefDataModel {

    var consumerId: String = ""
    var szName: String = ""
    var szNickName: String = ""
    var szRegion: String = ""

    init {
        initialize()
    }

    fun initialize() {
        this.consumerId = ""
        this.szName = ""
        this.szNickName = ""
        this.szRegion = ""
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "consumerId"))
            this.consumerId = UtilsString.parseString(dictionary["consumerId"])
        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "nickname"))
            this.szNickName = UtilsString.parseString(dictionary["nickname"])
        if(UtilsBaseFunction.isContainKey(dictionary, "region"))
            this.szRegion = UtilsString.parseString(dictionary["region"])
    }
}
