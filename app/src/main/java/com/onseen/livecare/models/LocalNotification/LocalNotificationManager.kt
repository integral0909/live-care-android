package com.onseen.livecare.models.LocalNotification

import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.CONSUMERS_LISTUPDATED
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.INVITE_LISTUPDATED
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.TRANSACTIONS_LISTUPDATED

class LocalNotificationManager : LocalNotificationObservable {

    private val observers: ArrayList<LocalNotificationObserver?> = ArrayList()

    companion object {
        private val sharedInstance: LocalNotificationManager = LocalNotificationManager()

        @Synchronized
        fun sharedInstance(): LocalNotificationManager {
            return sharedInstance
        }
    }

    override fun addObserver(o: LocalNotificationObserver?): Boolean {
        return o != null && !observers.contains(o) && observers.add(o)
    }

    override fun removeObserver(o: LocalNotificationObserver?): Boolean {
        return o != null && observers.remove(o)
    }

    override fun removeAllObservers() {
        observers.clear()
    }

    override fun notifyLocalNotification(notification: String) {
        for(observer in observers) {
            if(observer != null && notification.equals(CONSUMERS_LISTUPDATED)) {
                observer.consumerListUpdated()
            }
            if(observer != null && notification.equals(TRANSACTIONS_LISTUPDATED)) {
                observer.transactionListUpdated()
            }
            if(observer != null && notification.equals(INVITE_LISTUPDATED)) {
                observer.inviteListUpdated()
            }
        }
    }

    fun clearAll() {
        observers.clear()
    }
}
