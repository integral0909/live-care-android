package com.onseen.livecare.models.Consumer

import com.onseen.livecare.models.Communication.*
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.LocalNotification.LocalNotificationManager
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.CONSUMERS_LISTUPDATED
import org.json.JSONArray

class ConsumerManager {

    companion object {
        private val sharedInstance: ConsumerManager = ConsumerManager()

        @Synchronized
        fun sharedInstance(): ConsumerManager {
            return sharedInstance
        }
    }

    var arrayConsumers: ArrayList<ConsumerDataModel> = ArrayList()

    fun initialize() {
        this.arrayConsumers = ArrayList()
    }

    fun addConsumerIfNeeded(newConsumer: ConsumerDataModel) {
        if(!newConsumer.isValid()) return

        if (!newConsumer.isValidRegion()) return

        for(consumer in this.arrayConsumers) {
            if(consumer.id == newConsumer.id) return
        }

        this.arrayConsumers.add(newConsumer)
    }

    // MARK API Calls

    fun requestGetConsumers(callback: NetworkManagerResponse?) {
        val urlString = UrlManager.ConsumerApi.getConsumers()
        NetworkManager.GET(urlString, null, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess() && responseDataModel.payload.has("data")) {
                    val data: JSONArray = responseDataModel.payload.getJSONArray("data")
                    val array: ArrayList<ConsumerDataModel> = ArrayList()
                    for(i in 0 until data.length()) {
                        val dict = data.getJSONObject(i)
                        val consumer = ConsumerDataModel()
                        consumer.deserialize(dict)
                        if(consumer.isValid() && consumer.isValidRegion()) {
                            array.add(consumer)
                        }
                    }

                    val sortedArray = array.sortedWith(compareBy { it.szName })

                    arrayConsumers.clear()
                    arrayConsumers.addAll(sortedArray)
                }

                LocalNotificationManager.sharedInstance().notifyLocalNotification(CONSUMERS_LISTUPDATED)
                callback?.onComplete(responseDataModel)
            }
        })
    }
}