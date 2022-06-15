package com.onseen.livecare.models.Utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder

public class LCLocationManager: Service() {

    private var locationManager : LocationManager? = null

    companion object {
        private val sharedInstance: LCLocationManager = LCLocationManager()
        public var lat: Double = 0.0
        public var lng: Double = 0.0

        @Synchronized
        fun sharedInstance(): LCLocationManager {
            return sharedInstance
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mLocationListeners.add(LCLocationListener(LocationManager.GPS_PROVIDER))
        mLocationListeners.add(LCLocationListener(LocationManager.NETWORK_PROVIDER))

        initializeLocationManager()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initializeLocationManager() {
        UtilsGeneral.log("LocationManager initialized")

        if(locationManager == null) {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        startUpdatingLocation()
    }

    public fun startUpdatingLocation() {
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListeners[0]);
        } catch(ex: SecurityException) {
            UtilsGeneral.log("Security Exception, no location available");
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, mLocationListeners[1]);
        } catch(ex: SecurityException) {
            UtilsGeneral.log("Security Exception, no location available");
        }
    }

    public fun stopUpdatingLocation() {
        locationManager?.removeUpdates(mLocationListeners[0])
        locationManager?.removeUpdates(mLocationListeners[1])
    }

    private val mLocationListeners: ArrayList<LCLocationListener> = ArrayList()

    private class LCLocationListener(val provider: String): LocationListener {

        private var mLastLocation = Location(provider)

        override fun onLocationChanged(location: Location) {
            lat = location.latitude
            lng = location.longitude

            UtilsGeneral.log("[Location Manager - didUpdateLocations] - lat = " + lat + ", lng = " + lng)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onDestroy() {
        stopUpdatingLocation()
        super.onDestroy()
    }
}