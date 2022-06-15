package com.onseen.livecare.models.TripRequest.DataModel

import com.onseen.livecare.models.Base.BaseDataModel
import com.onseen.livecare.models.Consumer.DataModel.ConsumerRefDataModel
import com.onseen.livecare.models.Organization.DataModel.OrganizationRefDataModel
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsDate
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject
import java.util.*

class TripRequestDataModel: BaseDataModel() {
    public var routeId: String = ""

    public var szRecordLocator: String = ""
    public var szDescription: String = ""
    public var szNotes: String = ""

    public var szCaregiverName: String = ""
    public var szCaregiverContactNumber: String = ""

    public var isTbd: Boolean = false
    public var dateTime: Date? = null
    public var nEstimatedMiles: Int = 0
    public var isRoundTrip: Boolean = false
    public var dateReturn: Date? = null
    public var isReturnTbd: Boolean = false
    public var dateEnd: Date? = null

    public var isSunday: Boolean = false
    public var isMonday: Boolean = false
    public var isTuesday: Boolean = false
    public var isWednesday: Boolean = false
    public var isThursday: Boolean = false
    public var isFriday: Boolean = false
    public var isSaturday: Boolean = false

    public var geoPickup: GeoPointDataModel = GeoPointDataModel()
    public var geoDropoff: GeoPointDataModel = GeoPointDataModel()

    public var enumType: EnumTripRequestType = EnumTripRequestType.SHIPMENT
    public var enumTiming: EnumTripRequestTiming = EnumTripRequestTiming.ARRIVE_BY
    public var enumStatus: EnumTripRequestStatus = EnumTripRequestStatus.REQUESTED
    public var enumRecurringType: EnumTripRequestRecurringType = EnumTripRequestRecurringType.NONE

    public var refOrganization: OrganizationRefDataModel = OrganizationRefDataModel()
    public var refTransportOrganization: OrganizationRefDataModel = OrganizationRefDataModel()
    public var refConsumer: ConsumerRefDataModel = ConsumerRefDataModel()

    public var modelSpecialNeeds: SpecialNeedsDataModel = SpecialNeedsDataModel()

    // This property is used to validate / invalidate the TripRequest object. If the object is out-dated, we need to pull the object again
    public var outdated: Boolean = false

    init {
        initialize()
    }

    override fun initialize() {
        super.initialize()

        this.routeId = ""
        this.szRecordLocator = ""
        this.szDescription = ""
        this.szNotes = ""

        this.szCaregiverName = ""
        this.szCaregiverContactNumber = ""

        this.isTbd = false
        this.dateTime = null
        this.nEstimatedMiles = 0

        this.isRoundTrip = false
        this.dateReturn = null
        this.isReturnTbd = false
        this.dateEnd = null

        this.isSunday = false
        this.isMonday = false
        this.isTuesday = false
        this.isWednesday = false
        this.isThursday = false
        this.isFriday = false
        this.isSaturday = false

        this.geoPickup = GeoPointDataModel()
        this.geoDropoff = GeoPointDataModel()

        this.enumType = EnumTripRequestType.SHIPMENT
        this.enumTiming = EnumTripRequestTiming.ARRIVE_BY
        this.enumStatus = EnumTripRequestStatus.REQUESTED
        this.enumRecurringType = EnumTripRequestRecurringType.NONE

        this.refOrganization = OrganizationRefDataModel()
        this.refTransportOrganization = OrganizationRefDataModel()
        this.refConsumer = ConsumerRefDataModel()

        this.modelSpecialNeeds = SpecialNeedsDataModel()

        this.outdated = false
    }

    override fun deserialize(dictionary: JSONObject?) {
        this.initialize()
        if(dictionary == null) return

        super.deserialize(dictionary)

        if(UtilsBaseFunction.isContainKey(dictionary, "routeId")) {
            this.routeId = UtilsString.parseString(dictionary["routeId"])
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "recordLocator")) {
            this.szRecordLocator = UtilsString.parseString(dictionary["recordLocator"])
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "description")) {
            this.szDescription = UtilsString.parseString(dictionary["description"])
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "notes")) {
            this.szNotes = UtilsString.parseString("notes")
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "consumer")) {
            val consumer = dictionary.getJSONObject("consumer")
            this.refConsumer.deserialize(consumer)

            if(UtilsBaseFunction.isContainKey(consumer, "caregiver")) {
                val caregiver = consumer.getJSONObject("caregiver")
                if(UtilsBaseFunction.isContainKey(caregiver, "name"))
                    this.szCaregiverName = UtilsString.parseString(caregiver, "name")
                if(UtilsBaseFunction.isContainKey(caregiver, "contactNumber"))
                    this.szCaregiverContactNumber = UtilsString.parseString(caregiver["contactNumber"])
            }

            if(UtilsBaseFunction.isContainKey(consumer, "specialNeeds")) {
                val specialNeeds = consumer.getJSONObject("specialNeeds")
                this.modelSpecialNeeds.deserialize(specialNeeds)
            }
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "time")) {
            this.dateTime = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["time"]),
                EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "return")) {
            this.dateReturn = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["return"]),
                EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "end")) {
            this.dateEnd = UtilsDate.getDateTimeFromStringWithFormat(UtilsString.parseString(dictionary["end"]),
                EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC"))
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "tbd")) this.isTbd = UtilsString.parseBool(dictionary["tbd"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "returnTbd")) this.isReturnTbd = UtilsString.parseBool(dictionary["returnTbd"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "roundTrip")) this.isRoundTrip = UtilsString.parseBool(dictionary["roundTrip"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "sun")) this.isSaturday = UtilsString.parseBool(dictionary["sun"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "mon")) this.isMonday = UtilsString.parseBool(dictionary["mon"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "tue")) this.isTuesday = UtilsString.parseBool(dictionary["tue"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "wed")) this.isWednesday = UtilsString.parseBool(dictionary["wed"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "thu")) this.isThursday = UtilsString.parseBool(dictionary["thu"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "fri")) this.isFriday = UtilsString.parseBool(dictionary["fri"], false)
        if(UtilsBaseFunction.isContainKey(dictionary, "sat")) this.isSaturday = UtilsString.parseBool(dictionary["sat"], false)

        if(UtilsBaseFunction.isContainKey(dictionary, "pickup")) {
            val pickup = dictionary.getJSONObject("pickup")
            this.geoPickup.deserialize(pickup)
        }
        if(UtilsBaseFunction.isContainKey(dictionary, "delivery")) {
            val dropoff = dictionary.getJSONObject("delivery")
            this.geoDropoff.deserialize(dropoff)
        }

        if(UtilsBaseFunction.isContainKey(dictionary, "type"))
            this.enumType = EnumTripRequestType.fromString(UtilsString.parseString(dictionary["type"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "timing"))
            this.enumTiming = EnumTripRequestTiming.fromString(UtilsString.parseString(dictionary["timing"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "status"))
            this.enumStatus = EnumTripRequestStatus.fromString(UtilsString.parseString(dictionary["status"]))
        if(UtilsBaseFunction.isContainKey(dictionary, "recurringType"))
            this.enumRecurringType = EnumTripRequestRecurringType.fromString(UtilsString.parseString(dictionary["recurringType"]))

        if(UtilsBaseFunction.isContainKey(dictionary, "organization"))
            this.refOrganization.deserialize(dictionary.getJSONObject("organization"))
        if(UtilsBaseFunction.isContainKey(dictionary, "transport"))
            this.refTransportOrganization.deserialize(dictionary.getJSONObject("transport"))
    }

    public fun serializeForCreate(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("type", this.enumType.value)
        jsonObject.put("description", this.szDescription ?: "")
        jsonObject.put("timing", this.enumTiming.value)
        jsonObject.put("tbd", this.isTbd)
        jsonObject.put("time", UtilsDate.getStringFromDateTimeWithFormat(this.dateTime, EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC")))
        jsonObject.put("roundTrip", this.isRoundTrip)
        jsonObject.put("return", UtilsDate.getStringFromDateTimeWithFormat(this.dateReturn, EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC")))
        jsonObject.put("returnTbd", this.isReturnTbd)
        jsonObject.put("recurringType", this.enumRecurringType.value)
        jsonObject.put("sun", this.isSunday)
        jsonObject.put("mon", this.isMonday)
        jsonObject.put("tue", this.isTuesday)
        jsonObject.put("wed", this.isWednesday)
        jsonObject.put("thu", this.isThursday)
        jsonObject.put("fri", this.isFriday)
        jsonObject.put("sat", this.isSaturday)
        jsonObject.put("end", UtilsDate.getStringFromDateTimeWithFormat(this.dateEnd, EnumDateTimeFormat.yyyyMMdd_HHmmss_UTC.value, TimeZone.getTimeZone("UTC")))
        jsonObject.put("pickup", this.geoPickup.serialize())
        jsonObject.put("delivery", this.geoDropoff.serialize())

        val consumer = JSONObject()
        consumer.put("specialNeeds", this.modelSpecialNeeds.serialize())
        val caregiver = JSONObject()
        caregiver.put("name", this.szCaregiverName)
        caregiver.put("contactNumber", this.szCaregiverContactNumber)
        consumer.put("caregiver", caregiver)
        jsonObject.put("consumer", consumer)

        jsonObject.put("notes", this.szNotes)
        return jsonObject
    }

    public fun invalidate() {
        this.outdated = true
    }

    override fun isValid(): Boolean {
        return (!this.id!!.isEmpty() && this.enumStatus != EnumTripRequestStatus.DELETED &&
                this.enumStatus != EnumTripRequestStatus.CANCELLED && !this.outdated && this.enumType == EnumTripRequestType.SHIPMENT)
    }

    // MARK Utility Function

    public fun isScheduled(): Boolean {
        if(!this.isValid()) return false

        if(this.enumStatus == EnumTripRequestStatus.REQUESTED) return false
        return true
    }
}

enum class EnumTripRequestType(val value: String) {
    SERVICE("Services"),
    SHIPMENT("Shipment");

    companion object {
        fun fromString(role: String?): EnumTripRequestType {
            if(role == null || role.equals("")) return SERVICE

            if(role.toLowerCase().equals(SERVICE.value.toLowerCase())) {
                return SERVICE
            }
            if(role.toLowerCase().equals(SHIPMENT.value.toLowerCase())) {
                return SHIPMENT
            }

            return SERVICE
        }
    }
}

enum class EnumTripRequestStatus(val value: String) {
    SCHEDULED("Scheduled"),
    ASSIGNED("Assigned"),
    ACCEPTED("Accepted"),
    COMPLETED("Completed"),
    NOSHOW("No Show"),
    REQUESTED("Requested"),
    ENROUTE("En Route"),
    CANCELLED("Cancelled"),
    SAVED("Saved"),
    DELETED("Deleted"),
    ROUTING("Routing");

    companion object {
        fun fromString(role: String?): EnumTripRequestStatus {
            if(role == null || role.equals("")) return REQUESTED

            val values = arrayListOf<EnumTripRequestStatus>(SCHEDULED, ASSIGNED,
                ACCEPTED, COMPLETED, NOSHOW, NOSHOW,
                REQUESTED, ENROUTE, CANCELLED, SAVED,
                DELETED, ROUTING)

            for(value in values) {
                if(role.toLowerCase().equals(value.value.toLowerCase())) {
                    return value
                }
            }

            return REQUESTED
        }
    }
}

enum class EnumTripRequestTiming(val value: String) {
    ARRIVE_BY("Arrive By"),
    READY_BY("Ready By");

    companion object {
        fun fromString(role: String?): EnumTripRequestTiming {
            if(role == null || role.equals("")) return ARRIVE_BY

            if(role.toLowerCase().equals(ARRIVE_BY.value.toLowerCase())) {
                return ARRIVE_BY
            }
            if(role.toLowerCase().equals(READY_BY.value.toLowerCase())) {
                return READY_BY
            }

            return ARRIVE_BY
        }
    }
}

enum class EnumTripRequestWayType(val value: String) {
    ONE_WAY("One Way"),
    ROUND_TRIP("Round Trip");

    companion object {
        fun fromString(role: String?): EnumTripRequestWayType {
            if(role == null || role.equals("")) return ONE_WAY

            if(role.toLowerCase().equals(ONE_WAY.value.toLowerCase())) {
                return ONE_WAY
            }
            if(role.toLowerCase().equals(ROUND_TRIP.value.toLowerCase())) {
                return ROUND_TRIP
            }

            return ONE_WAY
        }
    }
}

enum class EnumTripRequestRecurringType(val value: String) {
    NONE(""),
    WEEKLY("Weekly");

    companion object {
        fun fromString(role: String?): EnumTripRequestRecurringType {
            if(role == null || role.equals("")) return NONE

            if(role.toLowerCase().equals(NONE.value.toLowerCase())) {
                return NONE
            }
            if(role.toLowerCase().equals(WEEKLY.value.toLowerCase())) {
                return WEEKLY
            }

            return NONE
        }
    }
}

