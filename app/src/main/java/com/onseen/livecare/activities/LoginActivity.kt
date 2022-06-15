package com.onseen.livecare.activities

import android.os.Bundle
import android.os.Handler
import com.onseen.livecare.R
import com.onseen.livecare.fragments.login.ForgotPasswordFragment
import com.onseen.livecare.fragments.login.LoginFragment
import com.onseen.livecare.fragments.login.RegisterFragment
import android.content.Intent
import androidx.fragment.app.Fragment
import com.onseen.livecare.models.AppManager.AppManager
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.User.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginActivity : BaseActivity() {

    private var pressedBackButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        showLoginFragment()
//        requestUserAutoLogin()
    }

    private fun requestUserAutoLogin() {
        val managerUser = UserManager.sharedInstance()
        managerUser.loadFromLocalstorage(this)
        if(managerUser.isLoggedIn() && managerUser.currentUser != null) {
            val email = managerUser.currentUser!!.szEmail
            val password = managerUser.currentUser!!.szPassword

            showProgressHUD()
            managerUser.requestUserLogin(email, password, object : NetworkManagerResponse {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    GlobalScope.launch(Dispatchers.Main) {
                        hideProgressHUD()
                        if(responseDataModel.isSuccess() && managerUser.isLoggedIn()) {
                            AppManager.sharedInstance().initializeManagersAfterLogin()
                            finish()
                        } else
                            showLoginFragment()
                    }
                }
            })
        } else {
            showLoginFragment()
        }
    }

    private fun showLoginFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        transaction.replace(R.id.fragmentContainer, LoginFragment(), "LoginFragment")
        transaction.commit()
    }

    fun gotoRegisterFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        transaction.replace(R.id.fragmentContainer, RegisterFragment(), "RegisterFragment")
        transaction.commit()
    }

    fun gotoResetPasswordFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        transaction.replace(R.id.fragmentContainer, ForgotPasswordFragment(), "ForgotPasswordFragment")
        transaction.commit()
    }

    fun backToLoginFragmentFrom(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        transaction.replace(R.id.fragmentContainer, LoginFragment(), "LoginFragment")
        transaction.commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.last()
        if (fragment is RegisterFragment ||
                fragment is ForgotPasswordFragment) {
            backToLoginFragmentFrom(fragment)
        } else {
            if (pressedBackButton) {
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
            } else {
                pressedBackButton = true
                showToast(getString(R.string.press_back_button))
                Handler().postDelayed({
                    pressedBackButton = false
                }, 3000)
            }
        }
    }
}
