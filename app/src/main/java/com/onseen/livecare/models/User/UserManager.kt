package com.onseen.livecare.models.User

import android.content.Context
import android.net.LocalServerSocket
import com.onseen.livecare.activities.LivecareApp
import com.onseen.livecare.models.Communication.*
import com.onseen.livecare.models.User.DataModel.UserDataModel
import com.onseen.livecare.models.Utils.LocalstorageManager
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONObject

class UserManager {
    val LOCALSTORAGE_KEY = "USER"

    var currentUser: UserDataModel? = null
    private var authToken: String = ""

    companion object {

        private val sharedInstance: UserManager = UserManager()
        val LOCALSTORAGE_KEY = "USER"

        @Synchronized
        fun sharedInstance(): UserManager {
            return sharedInstance
        }
    }

    init {

    }

    fun initialize() {
        this.currentUser = null
    }

    fun isLoggedIn(): Boolean {
        return (this.currentUser != null && !this.authToken.equals("") && this.currentUser!!.isValid())
    }

    fun logout(context: Context) {
        this.initialize()
        LocalstorageManager.deleteGlobalObject(context, LOCALSTORAGE_KEY)
    }

    fun getAuthToken(): String {
        return this.authToken
    }

    fun updateAuthToken(authToken: String?) {

        if(authToken == null) return

        if(!authToken.equals("")) {
            this.authToken = authToken
        }
    }

    // MARK: Localstorage

    fun saveToLocalstorage() {
        if(!this.isLoggedIn()) {
            return
        }

        val params = JSONObject()
        params.put("user_data", this.currentUser!!.serializeForLocalstorage())
        params.put("auth_token", this.authToken ?: "")
        LocalstorageManager.saveGlobalObject(LivecareApp.currentActivity()!!, params.toString(), UserManager.LOCALSTORAGE_KEY)
    }

    fun loadFromLocalstorage(context: Context) {
        val strParams = LocalstorageManager.loadGlobalObject(context, LOCALSTORAGE_KEY)
        if(strParams == null || strParams.equals("")) {
            this.currentUser = null
            return
        }

        val dictUserData: JSONObject = JSONObject(strParams)
        val userData: String = UtilsString.parseString(dictUserData["user_data"])
        this.authToken = UtilsString.parseString(dictUserData["auth_token"])

        val dictUser: JSONObject = JSONObject(userData)
        this.currentUser = UserDataModel()
        this.currentUser!!.deserializeFromLocalstorage(dictUser)
    }

    // MARK API Calls

    fun requestUserLogin(email: String, password: String, callback: NetworkManagerResponse) {
        val urlString = UrlManager.UserApi.login()
        val params: JSONObject = JSONObject()
        params.put("email", email.toLowerCase())
        params.put("password", password)

        NetworkManager.POST(urlString, params, EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value,  object: NetworkManagerResponse {

            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                currentUser = UserDataModel()
                currentUser!!.deserialize(responseDataModel.payload)
                currentUser!!.szPassword = password

                print("[User Login] token = " + authToken)
                callback.onComplete(responseDataModel)
            }
        })

    }

    fun requestUserSignUp(name: String, email: String, password: String, phone: String, callback: NetworkManagerResponse) {
        val urlString = UrlManager.UserApi.signup()
        val params: JSONObject = JSONObject()
        params.put("name", name)
        params.put("username", email.toLowerCase())
        params.put("email", email.toLowerCase())
        params.put("phoneNumber", phone)
        params.put("password", password)

        NetworkManager.POST(urlString, params, EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    currentUser = UserDataModel()
                    currentUser!!.deserialize(responseDataModel.payload)
                    currentUser!!.szPassword = password
                }
                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestForgotPassword(email: String, callback: NetworkManagerResponse) {
        val urlString = UrlManager.UserApi.forgotPassword()
        val params: JSONObject = JSONObject()
        params.put("email", email.toLowerCase())

        NetworkManager.POST(urlString, params, EnumNetworkAuthOptions.AUTH_NONE.value, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestUpdateUserWithDictionary(dictionary: JSONObject, callback: NetworkManagerResponse) {
        val urlString = UrlManager.UserApi.updateMyProfile()

        NetworkManager.PUT(urlString, dictionary, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    currentUser!!.deserialize(responseDataModel.payload)
                    if(dictionary.has("password")) {
                        val password = UtilsString.parseString(dictionary.get("password"))
                        currentUser!!.szPassword = password
                    }
                }
                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestGetMyProfile(callback: NetworkManagerResponse) {
        val urlString = UrlManager.UserApi.getMyProfile()

        NetworkManager.GET(urlString, null, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    currentUser!!.deserialize(responseDataModel.payload)
                }
                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestUpdateUserDeviceToken(deviceToken: String, callback: NetworkManagerResponse) {
        val urlString = UrlManager.UserApi.updateMyProfile()
        val params: JSONObject = JSONObject()
        params.put("deviceToken", deviceToken)

        NetworkManager.PUT(urlString, params, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                callback.onComplete(responseDataModel)
            }
        })
    }
}