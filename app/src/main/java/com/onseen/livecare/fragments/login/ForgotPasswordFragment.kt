package com.onseen.livecare.fragments.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.onseen.livecare.R
import com.onseen.livecare.activities.LoginActivity
import com.onseen.livecare.fragments.main.BaseFragment
import com.onseen.livecare.models.Communication.NetworkManagerResponse
import com.onseen.livecare.models.Communication.NetworkResponseDataModel
import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsBaseFunction
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForgotPasswordFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun viewAppeared() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener {
            hideKeyboard()
            (activity as? LoginActivity)?.backToLoginFragmentFrom(this)
        }

        btnReset.setOnClickListener {
            hideKeyboard()
            requestResetPassword()
        }
    }

    private fun requestResetPassword() {
        val email = edtEmail.text.toString()

        if(!UtilsBaseFunction.isValidEmail(email)) {
            showToast("Please enter valid email address")
            return
        }

        showProgressHUD()
        UserManager.sharedInstance().requestForgotPassword(email, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                GlobalScope.launch(Dispatchers.Main) {
                    hideProgressHUD()
                    if(responseDataModel.isSuccess()) {
                        UtilsBaseFunction.showAlert(
                            activity!!,
                            "Confirmation",
                            "You should receive an email to reset password soon.")
                    } else {
                        showToast(responseDataModel.getBeautifiedErrorMessage())
                    }
                }
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ForgotPasswordFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
