package com.onseen.livecare.activities

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.onseen.livecare.BuildConfig
import com.onseen.livecare.interfaces.LogoutListener
import com.onseen.livecare.models.Utils.UtilsConfig
import io.fabric.sdk.android.Fabric
import java.util.*

class LivecareApp : MultiDexApplication() {

    private var listener: LogoutListener? = null
    private var timer: Timer? = null
    final var timeoutSecond: Long = 60 * 60 * 1000

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    init {
        instance = this
    }

    val mActivityLifecycleCallbacks = LCActivityLifecycleCallbacks()

    override fun onCreate() {
        super.onCreate()

        if (UtilsConfig.shouldEnableFabric) {
            Fabric.with(this, Crashlytics())
        } else {
            val crashlytics = Crashlytics.Builder().disabled(BuildConfig.DEBUG).build()
            Fabric.with(this, crashlytics)
        }

        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    // Timeout Session

    fun startUserSession() {
        cancelTimer()

        timer = Timer()
        timer!!.schedule(object: TimerTask() {
            override fun run() {
                listener!!.onSessionLogout()
            }
        }, timeoutSecond)
    }

    fun registerSesstionListener(listener: LogoutListener) {
        this.listener = listener
    }

    fun onUserInteracted() {
        startUserSession()
    }

    fun cancelTimer() {
        if(timer != null) timer!!.cancel()
    }

    companion object {
        private var instance: LivecareApp? = null

        fun currentActivity(): Activity? {

            return instance!!.mActivityLifecycleCallbacks.currentActivity
        }
    }

}