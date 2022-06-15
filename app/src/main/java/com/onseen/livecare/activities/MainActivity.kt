package com.onseen.livecare.activities

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.onseen.livecare.R
import com.onseen.livecare.adapters.MainPagerAdapter
import com.onseen.livecare.models.AppManager.AppManager
import com.onseen.livecare.models.User.UserManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.onseen.livecare.fragments.main.BaseFragment
import com.onseen.livecare.interfaces.LogoutListener
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.Invite.DataModel.InviteDataModel
import com.onseen.livecare.models.Invite.InviteManager
import com.onseen.livecare.models.LocalNotification.LocalNotificationManager
import com.onseen.livecare.models.LocalNotification.LocalNotificationObserver
import com.onseen.livecare.models.Utils.LCLocationManager
import com.onseen.livecare.models.Utils.UtilsGeneral
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, LocalNotificationObserver, LogoutListener {

    private lateinit var pagerAdapter: MainPagerAdapter
    private var isInviteModalShowing = false
    private var modelInvite: InviteDataModel? = null
    private var dateOnBackground: Date? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mToolBarNavigationListenerIsRegistered: Boolean = false

    private val LOCATION_PEERMISSION_TAG = "Location_Permission"
    private val LOCATION_REQUEST_CODE = 101
    private lateinit var intentLocation: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener true
        }

        pagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 4

        initializeDrawerLayout()

        bottomNavView.selectedItemId = 0
        title = getString(R.string.tab_consumers)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setCheckedItem(R.id.tab_consumers);

        //Show Login screen
        showLoginActivity()

        // Register Local Notification
        LocalNotificationManager.sharedInstance().addObserver(this)

        // Session Timeout
        (application as LivecareApp).registerSesstionListener(this)


        intentLocation = Intent(this@MainActivity, LCLocationManager::class.java)
    }

    private fun initializeDrawerLayout() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        mDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle!!.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun updateUserInfo() {
        if(!UserManager.sharedInstance().isLoggedIn()) return

        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val txtUserName: TextView = headerView.findViewById(R.id.txtUserName)
        val txtUserEmail: TextView = headerView.findViewById(R.id.txtUserEmail)

        txtUserName.text = UserManager.sharedInstance().currentUser!!.szName
        txtUserEmail.text = UserManager.sharedInstance().currentUser!!.szEmail
        txt_app_version.text = "App Info:  " + UtilsGeneral.getBeautifiedAppVersionInfo()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currentFragment = pagerAdapter.currentFragment ?: return
            currentFragment.onBackPressed()
        }
    }

    fun showLoginActivity() {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
    }

    fun logout() {
        showProgressHUD()
        UserManager.sharedInstance().logout(applicationContext)
        AppManager.sharedInstance().initializeManagersAfterLogout()

        showLoginActivity()

        Timer().schedule(1000){
            GlobalScope.launch(Dispatchers.Main) {
                hideProgressHUD()
                title = getString(R.string.tab_consumers)
                val navView: NavigationView = findViewById(R.id.nav_view)
                viewPager.setCurrentItem(0, false)
                navView.setCheckedItem(R.id.tab_consumers);
            }
        }
    }

    // Show / Hide back button

    fun showBackButton() {

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mDrawerToggle!!.isDrawerIndicatorEnabled = false
        if (!mToolBarNavigationListenerIsRegistered) {
            mDrawerToggle!!.toolbarNavigationClickListener = View.OnClickListener { onBackPressed() }

            mToolBarNavigationListenerIsRegistered = true
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideBackButton() {

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        mDrawerToggle!!.isDrawerIndicatorEnabled = true
        mDrawerToggle!!.toolbarNavigationClickListener = null
        mToolBarNavigationListenerIsRegistered = false
    }

    override fun onResume() {
        super.onResume()

        // Session Timeout
        if(UserManager.sharedInstance().isLoggedIn()) {
            if(this.dateOnBackground == null) {
                (application as LivecareApp).startUserSession()
            } else {
                val now = Date()
                val elapsed = now.time - this.dateOnBackground!!.time
                if(elapsed > (application as LivecareApp).timeoutSecond)
                    onSessionLogout()
                else {
                    this.dateOnBackground = null
                    (application as LivecareApp).startUserSession()
                }
            }
        }

        updateUserInfo()
    }

    override fun onPause() {
        super.onPause()

        if(UserManager.sharedInstance().isLoggedIn()) {
            this.dateOnBackground = Date()
            (application as LivecareApp).cancelTimer()
        }
    }

    override fun onSessionLogout() {
        this.dateOnBackground = null

        GlobalScope.launch(Dispatchers.Main) {
            logout()
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        if(UserManager.sharedInstance().isLoggedIn())
            (application as LivecareApp).onUserInteracted()
    }

    // Invites

    override fun inviteListUpdated() {
        super.inviteListUpdated()

        checkPendingInvites()
    }

    private fun checkPendingInvites() {
        val array = InviteManager.sharedInstance().getPendingInvites()
        if(array.size == 0) return

        this.modelInvite = array[0]
        this.presentInvitePrompt()
    }

    private fun presentInvitePrompt() {
        if (this.modelInvite == null) return

        if (isInviteModalShowing) return

        this.isInviteModalShowing = true

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.modal_fragment_pending_invite)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvProfile = dialog.findViewById(R.id.img_profile) as ImageView
        val tvTitle = dialog.findViewById(R.id.claim_description) as TextView
        val btnDecline = dialog.findViewById(R.id.button_decline) as TextView
        val btnAccept = dialog.findViewById(R.id.button_accept) as TextView
        tvTitle.text = "You have received an invitation from " + modelInvite!!.organizationName
        btnDecline.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                dialog.dismiss()
                declineInvite()
            }
        }

        btnAccept.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                dialog.dismiss()
                acceptInvite()
            }
        }
        dialog.show()
    }

    private fun acceptInvite() {
        if(this.modelInvite == null) return

        this.isInviteModalShowing = false

        showProgressHUD()
        InviteManager.sharedInstance().requestAcceptInvite(this.modelInvite!!, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        UtilsGeneral.log("Invite accepted.")
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    private fun declineInvite() {
        if(this.modelInvite == null) return

        showProgressHUD()
        InviteManager.sharedInstance().requestDeclineInvite(this.modelInvite!!, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                hideProgressHUD()
                if(responseDataModel.isSuccess()) {
                    UtilsGeneral.log("Invite declined.")
                } else {
                    showToast(responseDataModel.getBeautifiedErrorMessage())
                }
            }
        })
    }

    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        var frg = pagerAdapter.currentFragment!!.childFragmentManager.fragments.last()
        frg.userVisibleHint = false

        when (item.itemId) {
            R.id.tab_consumers -> {
                viewPager.setCurrentItem(0, false)
            }
            R.id.tab_transactions -> {
                viewPager.setCurrentItem(1, false)
            }
            R.id.tab_trasportation -> {
                viewPager.setCurrentItem(2, false)
            }
            R.id.tab_appointment -> {
                viewPager.setCurrentItem(3, false)
            }
            R.id.nav_message -> {

            }
            R.id.nav_logout -> {
                logout()
            }
        }

        frg = pagerAdapter.currentFragment!!.childFragmentManager.fragments.last() as BaseFragment
        title = frg.getNavTitle()
        frg.userVisibleHint = true

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupPermissions() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions,0)
        } else {
            startService(intentLocation)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {
                    startService(intentLocation)
                }
            }
        }
    }

    override fun onDestroy() {
        LocalNotificationManager.sharedInstance().removeObserver(this)
        stopService(intentLocation)

        super.onDestroy()
    }
}
