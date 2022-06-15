package com.onseen.livecare.models.TripRequest

import com.onseen.livecare.models.Communication.*
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.TripRequest.DataModel.EnumTripRequestStatus
import com.onseen.livecare.models.TripRequest.DataModel.TripRequestDataModel

class TripRequestManager {

    public var arrayTripRequests: ArrayList<TripRequestDataModel> = ArrayList()

    companion object {
        private val sharedInstance: TripRequestManager = TripRequestManager()

        @Synchronized
        fun sharedInstance(): TripRequestManager {
            return sharedInstance
        }
    }

    init {
        initialize()
    }

    private fun initialize() {
        this.arrayTripRequests = ArrayList()
    }

    public fun addTripRequestIfNeeded(newTripRequest: TripRequestDataModel) {
        if(newTripRequest.enumStatus == EnumTripRequestStatus.CANCELLED ||
                newTripRequest.enumStatus == EnumTripRequestStatus.DELETED) {
            this.deleteTripRequestIfNeeded(newTripRequest)
            return
        }

        if(!newTripRequest.isValid()) return

        var index: Int = 0
        for(tripRequest in this.arrayTripRequests) {
            if(tripRequest.id.equals(newTripRequest.id)) {
                this.arrayTripRequests[index] = newTripRequest
                tripRequest.invalidate()
                return
            }
            index = index + 1
        }
        this.arrayTripRequests.add(newTripRequest)
    }

    public fun appendTripRequestsFromArray(tripRequests: ArrayList<TripRequestDataModel>?) {
        if(tripRequests == null) return

        for(tripRequest in tripRequests) {
            this.addTripRequestIfNeeded(tripRequest)
        }
    }

    public fun deleteTripRequestIfNeeded(deleteTripRequest: TripRequestDataModel) {
        var index: Int = 0
        for(tripRequest in this.arrayTripRequests) {
            if(tripRequest.id!!.equals(this.arrayTripRequests)) {
                tripRequest.invalidate()
                this.arrayTripRequests.removeAt(index)
                return
            }
            index = index + 1
        }
    }

    public fun getTripRequestById(tripRequestId: String): TripRequestDataModel? {
        for(tripRequest in this.arrayTripRequests) {
            if(tripRequest.id.equals(tripRequestId)) {
                return tripRequest
            }
        }

        return null
    }

    public fun getTripRequestsByOrganizationId(organizationId: String): ArrayList<TripRequestDataModel> {
        val array: ArrayList<TripRequestDataModel> = ArrayList()
        for(tripRequest in this.arrayTripRequests) {
            if(tripRequest.refOrganization.organizationId.equals(organizationId)) {
                array.add(tripRequest)
            }
        }
        return array
    }

    public fun getTripRequestsByConsumerId(consumerId: String): ArrayList<TripRequestDataModel> {
        val array: ArrayList<TripRequestDataModel> = ArrayList()
        for(tripRequest in this.arrayTripRequests) {
            if(tripRequest.refConsumer.consumerId.equals(consumerId)) {
                array.add(tripRequest)
            }
        }

        return array
    }

    // MARK API calls

    public fun requestGetTripRequestsForConsumer(consumer: ConsumerDataModel, forceReload: Boolean, callback: NetworkManagerResponse?) {
        if(!forceReload) {
            val array: ArrayList<TripRequestDataModel> = this.getTripRequestsByConsumerId(consumer.id!!) ?: ArrayList()
            if(array.size > 0) {
                val response = NetworkResponseDataModel.instanceForSuccess()
                callback?.onComplete(response)
                return
            }
        }

        val urlString: String = UrlManager.TripRequestApi.getTripRequestsByOrganizationId(consumer.organizationId)
        NetworkManager.GET(urlString, null, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess() && responseDataModel.payload.has("data")) {
                    val data = responseDataModel.payload.getJSONArray("data")
                    for(i in 0..data.length()-1) {
                        val dict = data.getJSONObject(i)
                        val tripRequest = TripRequestDataModel()
                        tripRequest.deserialize(dict)
                        addTripRequestIfNeeded(tripRequest)
                    }

                    val result = NetworkResponseDataModel.instanceForSuccess()
                    result.parsedObject = getTripRequestsByConsumerId(consumer.id!!)
                    callback?.onComplete(result)
                } else {
                    callback?.onComplete(responseDataModel)
                }
            }
        })
    }

    public fun requestCreateTripRequestForConsumer(consumer: ConsumerDataModel, tripRequest: TripRequestDataModel, callback: NetworkManagerResponse?) {
        val urlString = UrlManager.TripRequestApi.createTripRequest(consumer.organizationId, consumer.id!!)
        NetworkManager.POST(urlString, tripRequest.serializeForCreate(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    requestGetTripRequestsForConsumer(consumer, true, callback)
                } else {
                    callback?.onComplete(responseDataModel)
                }
            }
        })
    }
}