package com.onseen.livecare.fragments.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.onseen.livecare.R
import com.onseen.livecare.activities.LoginActivity
import com.onseen.livecare.fragments.main.BaseFragment
import com.onseen.livecare.models.AppManager.AppManager
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            hideKeyboard()
            (activity as? LoginActivity)?.backToLoginFragmentFrom(this)
        }

        btnRegister.setOnClickListener {
            hideKeyboard()
            requestSignup()
        }
    }

    override fun viewAppeared() {

    }

    private fun requestSignup() {
        val fullName = edtName.text.toString()
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()
        val confirmPassword = edtConfirmPassword.toString()

        if(fullName.isEmpty()) {
            showToast("Please enter your full name.")
            return
        }

        if(!UtilsBaseFunction.isValidEmail(email)) {
            showToast("Please enter valid email address.")
            return
        }

        if(password.isEmpty()) {
            showToast("Please enter password.")
            return
        }

        if(confirmPassword.equals(password)) {
            showToast("Confirm Password does not match.")
            return
        }

        showProgressHUD()
        val managerUser = UserManager.sharedInstance()

        managerUser.requestUserSignUp(fullName, email, password, "", object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        managerUser.saveToLocalstorage()
                        gotoMainFragment()
                    } else
                        responseDataModel.getBeautifiedErrorMessage()
                }
            }
        })
    }

    private fun gotoMainFragment() {
        activity!!.finish()
        AppManager.sharedInstance().initializeManagersAfterLogin()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
