package com.onseen.livecare.models.Base

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONArray
import org.json.JSONObject

class CoordDataModel {
    var enumType: CoordType = CoordType.POINT
    var fLatitude: Double = 0.0
    var fLongitude: Double = 0.0

    init {
        initialize()
    }

    fun initialize() {
        this.enumType = CoordType.POINT
        this.fLatitude = 0.0
        this.fLongitude = 0.0
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()
        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "coordinates")) {
            val coord: JSONArray? = dictionary.getJSONArray("coordinates")
            if(coord != null && coord.length() == 2) {
                this.fLatitude = UtilsString.parseDouble(coord.get(1), 0.0)
                this.fLongitude = UtilsString.parseDouble(coord.get(0), 0.0)
            }
        }
    }

    fun serialize(): JSONObject {
        val json: JSONObject = JSONObject()
        json.put("type", "Point")

        val coordArray: JSONArray = JSONArray()
        coordArray.put(this.fLongitude)
        coordArray.put(this.fLatitude)
        json.put("coordinates", coordArray)

        return json
    }
}

enum class CoordType(val value: String) {
    POINT("Point")
}
