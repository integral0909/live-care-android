package com.onseen.livecare.activities

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kaopiz.kprogresshud.KProgressHUD

open class BaseActivity : AppCompatActivity() {

    private var progressHUD: KProgressHUD? = null

    fun showProgressHUD() {
        progressHUD = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD!!.show()
    }

    fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        //Hide the soft keyboard
        if (imm != null) {
            val view = this.currentFocus
            if (view != null)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideProgressHUD() {
        if(progressHUD!!.isShowing)
            progressHUD!!.dismiss()
    }

    fun showToast(message: String) {
        this.runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        if(progressHUD != null && progressHUD!!.isShowing)
            progressHUD!!.dismiss()

        super.onDestroy()
    }

}