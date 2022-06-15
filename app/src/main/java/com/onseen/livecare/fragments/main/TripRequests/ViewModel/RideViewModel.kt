package com.onseen.livecare.fragments.main.TripRequests.ViewModel

import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.TripRequest.DataModel.*
import com.onseen.livecare.models.Utils.EnumDateTimeFormat
import com.onseen.livecare.models.Utils.UtilsDate
import java.util.*
import kotlin.collections.ArrayList

class RideViewModel {
    public var modelConsumer: ConsumerDataModel? = null
    public var geoPickup: GeoPointDataModel = GeoPointDataModel()
    public var geoDropoff: GeoPointDataModel = GeoPointDataModel()

    public var enumWayType: EnumTripRequestWayType = EnumTripRequestWayType.ONE_WAY
    public var enumTiming: EnumTripRequestTiming = EnumTripRequestTiming.ARRIVE_BY

    public var date: Date? = null
    public var szTime: String = ""
    public var dateReturnDate: Date? = null
    public var szReturnTime: String = ""

    public var isRecurring: Boolean = false
    public var dateRepeatUntil: Date? = null
    public var flagWeekdays: ArrayList<Boolean> = arrayListOf(false, false, false, false, false, false, false)
    public var isReturnTbd: Boolean = false

    public var szContactName: String = ""
    public var szContactPhone: String = ""

    public var modelSpecialNeeds: SpecialNeedsDataModel = SpecialNeedsDataModel()

    init {
        initialize()
    }

    private fun initialize() {
        this.modelConsumer = null
        this.enumWayType = EnumTripRequestWayType.ONE_WAY
        this.enumTiming = EnumTripRequestTiming.ARRIVE_BY

        this.date = null
        this.szTime = ""
        this.dateReturnDate = null
        this.szReturnTime = ""

        this.szContactName = ""
        this.szContactPhone = ""

        this.modelSpecialNeeds = SpecialNeedsDataModel()

        this.isRecurring = false
        this.dateRepeatUntil = null
        this.flagWeekdays = arrayListOf(false, false, false, false, false, false, false)
        this .isReturnTbd = false
    }

    public fun mergeDataTime(date: Date?, time: String): Date? {
        var sz = UtilsDate.getStringFromDateTimeWithFormat(date, EnumDateTimeFormat.MMddyyyy.value, null)
        sz = sz + " " + time
        return UtilsDate.getDateTimeFromStringWithFormat(sz, EnumDateTimeFormat.MMddyyyy_hhmma.value, null)
    }

    public fun toDataModel(): TripRequestDataModel {
        val tripRequest = TripRequestDataModel()

        tripRequest.enumType = EnumTripRequestType.SHIPMENT
        tripRequest.szDescription = ""
        tripRequest.szNotes = ""
        tripRequest.enumTiming = enumTiming
        tripRequest.dateTime = mergeDataTime(this.date, this.szTime)

        if(this.enumWayType == EnumTripRequestWayType.ROUND_TRIP) {
            tripRequest.isRoundTrip = true
            tripRequest.dateReturn = mergeDataTime(this.dateReturnDate, szReturnTime)
            tripRequest.isReturnTbd = this.isReturnTbd
        }
        if(this.isRecurring) {
            tripRequest.enumRecurringType = EnumTripRequestRecurringType.WEEKLY
            tripRequest.isSunday = this.flagWeekdays[0]
            tripRequest.isMonday = this.flagWeekdays[1]
            tripRequest.isTuesday = this.flagWeekdays[2]
            tripRequest.isWednesday = this.flagWeekdays[3]
            tripRequest.isThursday = this.flagWeekdays[4]
            tripRequest.isFriday = this.flagWeekdays[5]
            tripRequest.isSaturday = this.flagWeekdays[6]

            tripRequest.dateEnd = this.mergeDataTime(this.dateRepeatUntil, this.szTime)
        }

        tripRequest.geoPickup = this.geoPickup
        tripRequest.geoDropoff = this.geoDropoff

        tripRequest.szCaregiverName = this.szContactName
        tripRequest.szCaregiverContactNumber = this.szContactPhone
        tripRequest.modelSpecialNeeds = this.modelSpecialNeeds

        tripRequest.refConsumer.consumerId = this.modelConsumer!!.id!!
        tripRequest.refOrganization.organizationId = this.modelConsumer!!.organizationId

        return tripRequest
    }
}