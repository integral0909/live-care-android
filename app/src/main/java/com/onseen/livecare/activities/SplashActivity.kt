package com.onseen.livecare.activities

import android.os.Bundle
import android.content.Intent
import android.os.Handler
import com.onseen.livecare.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    private val SPLASH_TIME_OUT:Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            GlobalScope.launch(Dispatchers.Main) {
                gotoMainActivity()
            }
        }, SPLASH_TIME_OUT)
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}