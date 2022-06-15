package com.onseen.livecare.fragments.main

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.onseen.livecare.activities.LivecareApp
import com.onseen.livecare.activities.MainActivity

abstract class BaseFragment: Fragment() {

    private var progressHUD: KProgressHUD? = null
    private var isActiveFragment = false
    private var navTitle = ""

    protected fun showProgressHUD() {
        if (progressHUD != null && progressHUD!!.isShowing) return
        progressHUD = KProgressHUD.create(LivecareApp.currentActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD!!.show()
    }

    protected fun hideProgressHUD() {
        if(progressHUD!!.isShowing)
            progressHUD!!.dismiss()
    }

    protected fun showToast(message: String) {
        LivecareApp.currentActivity()!!.runOnUiThread {
            Toast.makeText(LivecareApp.currentActivity(), message, Toast.LENGTH_LONG).show()
        }
    }

    protected fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        val view = activity?.currentFocus ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public abstract fun viewAppeared()

    fun getNavTitle(): String {
        return navTitle
    }

    protected fun showBackButton() {
        val currentActivity = LivecareApp.currentActivity() ?: return
        if (currentActivity is MainActivity)
            currentActivity.showBackButton()
    }

    protected fun hideBackButton() {
        val currentActivity = LivecareApp.currentActivity() ?: return
        if (currentActivity is MainActivity)
            currentActivity.hideBackButton()
    }

    protected fun setNavTitle(str: String, isTabFragment:Boolean) {
        navTitle = str
        if(isTabFragment) return

        activity?.title = navTitle
    }

    override fun onDestroy() {
        if(progressHUD != null && progressHUD!!.isShowing)
            progressHUD!!.dismiss()
        super.onDestroy()
    }
}