package com.onseen.livecare.models.User.DataModel

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class UserRefDataModel {
    var userId: String = ""
    var szName: String = ""
    var szUsername: String = ""
    var szEmail: String = ""

    init {
        initialize()
    }

    fun initialize() {
        this.userId = ""
        this.szName = ""
        this.szUsername = ""
        this.szEmail = ""
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "userId"))
            this.userId = UtilsString.parseString(dictionary["userId"])
        if(UtilsBaseFunction.isContainKey(dictionary, "name"))
            this.szName = UtilsString.parseString(dictionary["name"])
        if(UtilsBaseFunction.isContainKey(dictionary, "username"))
            this.szUsername = UtilsString.parseString(dictionary["username"])
        if(UtilsBaseFunction.isContainKey(dictionary, "email"))
            this.szEmail = UtilsString.parseString(dictionary["email"])
    }
}