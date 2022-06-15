package com.onseen.livecare.models.LocalNotification

interface LocalNotificationObservable {
    fun addObserver(o: LocalNotificationObserver?): Boolean
    fun removeObserver(o: LocalNotificationObserver?): Boolean

    fun removeAllObservers()
    fun notifyLocalNotification(notification: String)
}