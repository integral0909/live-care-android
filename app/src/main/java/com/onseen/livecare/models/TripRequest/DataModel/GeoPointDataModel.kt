package com.onseen.livecare.models.TripRequest.DataModel

import com.google.android.gms.maps.model.LatLng
import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs

class GeoPointDataModel {
    public var enumType: EnumGeoPointType = EnumGeoPointType.POINT
    public var fLatitude: Float = 0.0f
    public var fLongitude: Float = 0.0f
    public var szAddress: String = ""

    init {
        initialize()
    }

    public fun initialize() {
        
        this.fLatitude = 0.0f
        this.fLongitude = 0.0f
        this.enumType = EnumGeoPointType.POINT
        this.szAddress = ""
    }

    public fun deserialize(dictionary: JSONObject?) {
        this.initialize()
        if(dictionary == null) return

        if(UtilsBaseFunction.isContainKey(dictionary, "streetAddress")) {
            this.szAddress = UtilsString.parseString(dictionary["streetAddress"])
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "location")) {
            val location = dictionary.getJSONObject("location")
            this.enumType = EnumGeoPointType.fromString((UtilsString.parseString(location["type"])))
            if(UtilsBaseFunction.isContainKey(location, "coordinates")) {
                val coords = location.getJSONArray("coordinates")
                if(coords.length() == 2) {
                    this.fLongitude = UtilsString.parseFloat(coords[0], 0.0f)
                    this.fLatitude = UtilsString.parseFloat(coords[1], 0.0f)
                }
            }
        }
    }

    public fun serialize(): JSONObject {
        val jsonObject = JSONObject()

        val coord = JSONObject()
        coord.put("type", this.enumType.value)
        val coordinates = JSONArray()
        coordinates.put(this.fLongitude)
        coordinates.put(this.fLatitude)
        coord.put("coordinates", coordinates)

        jsonObject.put("location", coord)
        jsonObject.put("streetAddress", this.szAddress ?: "")

        return jsonObject
    }

    public fun isValid(): Boolean {
        if(abs(this.fLongitude) < 0.1 && abs(this.fLatitude) < 0.1) return false
        return true
    }

    public fun isSame(otherPoint: GeoPointDataModel) : Boolean {
        if(this.isValid() == false || otherPoint.isValid() == false) {
            return false
        }

        if(abs(this.fLongitude - otherPoint.fLongitude) > 0.000001) {
            return false
        }

        if(abs(this.fLatitude - otherPoint.fLatitude) > 0.000001) {
            return false
        }

        return true
    }

    private var coord = LatLng(0.0, 0.0)
    public fun getCoord(): LatLng {
        return LatLng(this.fLatitude.toDouble(), this.fLongitude.toDouble())
    }
}

enum class EnumGeoPointType(val value: String) {
    NONE(""),
    POINT("Point");

    companion object {
        fun fromString(role: String?): EnumGeoPointType {
            if(role == null || role.equals("")) return NONE

            if(role.toLowerCase().equals(NONE.value.toLowerCase())) {
                return NONE
            }
            if(role.toLowerCase().equals(POINT.value.toLowerCase())) {
                return POINT
            }

            return POINT
        }
    }
}