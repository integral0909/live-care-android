package com.onseen.livecare.models.TripRequest.DataModel

import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*

class SpecialNeedsDataModel {
    public var isWheelchair: Boolean = false
    public var isBlind: Boolean = false
    public var isDeaf: Boolean = false
    public var isWalker: Boolean = false
    public var isServiceAnimal: Boolean = false

    init {
        initialize()
    }

    private fun initialize() {
        this.isWheelchair = false
        this.isBlind = false
        this.isDeaf = false
        this.isWalker = false
        this.isServiceAnimal = false
    }

    fun deserialize(dictionary: JSONObject?) {
        this.initialize()

        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "wheelchair")) {
            this.isWheelchair = UtilsString.parseBool(dictionary["wheelchair"], false)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "blind")) {
            this.isBlind = UtilsString.parseBool(dictionary["blind"], false)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "deaf")) {
            this.isDeaf = UtilsString.parseBool(dictionary["deaf"], false)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "walker")) {
            this.isWalker = UtilsString.parseBool(dictionary["walker"], false)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "serviceAnimal")) {
            this.isServiceAnimal = UtilsString.parseBool(dictionary["serviceAnimal"], false)
        }
    }

    fun serialize() : JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("wheelchair", this.isWheelchair)
        jsonObject.put("blind", this.isBlind)
        jsonObject.put("deaf", this.isDeaf)
        jsonObject.put("walker", this.isWalker)
        jsonObject.put("serviceAnimal", this.isServiceAnimal)

        return jsonObject
    }
}