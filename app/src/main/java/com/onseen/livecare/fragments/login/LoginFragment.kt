package com.onseen.livecare.fragments.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.onseen.livecare.R
import com.onseen.livecare.activities.LivecareApp
import com.onseen.livecare.activities.LoginActivity
import com.onseen.livecare.fragments.main.BaseFragment
import com.onseen.livecare.models.AppManager.AppManager
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import com.onseen.livecare.models.Utils.UtilsString
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {

            if(!checkValidation()) return@setOnClickListener

            showProgressHUD()
            UserManager.sharedInstance().requestUserLogin(edtEmail.text.toString(), edtPassword.text.toString(), object : NetworkManagerResponse  {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    GlobalScope.launch(Dispatchers.Main) {
                        hideProgressHUD()

                        if(responseDataModel.isSuccess()) {
                            if(UserManager.sharedInstance().currentUser != null && UserManager.sharedInstance().currentUser!!.isValid()) {
                                gotoMainFragment()
                            } else {
                                showToast("User is not active.")
                            }
                        } else if(responseDataModel.errorCode == UtilsString.ErrorCode.USER_LOGIN_INVALIDCREDENTIALS.value){
                            showToast("The email or password is incorrect. Please try again.")
                        } else {
                            showToast(responseDataModel.getBeautifiedErrorMessage())
                        }
                    }
                }
            })
        }

        btnRegister.setOnClickListener {
            (activity as? LoginActivity)?.gotoRegisterFragment()
        }

        btnForgotPassword.setOnClickListener {
            (activity as? LoginActivity)?.gotoResetPasswordFragment()
        }
    }

    override fun viewAppeared() {

    }

    fun gotoMainFragment() {
        activity?.finish()
        AppManager.sharedInstance().initializeManagersAfterLogin()
    }

    fun checkValidation(): Boolean {
        if(edtEmail.text.toString().equals("")) {
            showToast("Please input email address")
            return false
        }
        if(!UtilsBaseFunction.isValidEmail(edtEmail.text.toString())) {
            showToast("Please inupt valid email address")
            return false
        }
        if(edtPassword.text.toString().equals("")) {
            showToast("Please input password")
            return false
        }
        return true
    }

}
