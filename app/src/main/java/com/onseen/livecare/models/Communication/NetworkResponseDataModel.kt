package com.onseen.livecare.models.Communication

import com.github.kittinunf.fuel.core.HeaderValues
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isSuccessful
import com.onseen.livecare.models.Utils.UtilsGeneral
import com.onseen.livecare.models.Utils.UtilsString
import com.onseen.livecare.models.User.UserManager
import org.json.JSONObject
import java.lang.Exception

class NetworkResponseDataModel {

    var payload: JSONObject = JSONObject()
    var parsedObject: Any? = null
    var code: Int = EnumNetworkResponseCode.CODE_200_OK.value

    var errorMessage: String = ""
    var errorCode: String = ""

    constructor()

    constructor(code:Int, mMessage: String?) {
        this.code = code
        this.errorMessage = mMessage as String
    }

    fun initialize() {
        this.payload = JSONObject()
        this.parsedObject = null
        this.code = EnumNetworkResponseCode.CODE_200_OK.value

        this.errorMessage = ""
        this.errorCode = ""
    }

    fun isSuccess(): Boolean {
        return (this.code == EnumNetworkResponseCode.CODE_200_OK.value ||
                this.code == EnumNetworkResponseCode.CODE_201_CREATED.value)
    }

    fun getBeautifiedErrorMessage(): String {
        if(this.isSuccess()) return ""
        if(!this.errorMessage.equals("")) return this.errorMessage
        return "Sorry, we've encountered an unknown error."
    }

    companion object {
        fun instanceFromDataResponse(response: Response, shouldUpdateToken:Boolean): NetworkResponseDataModel {
            if(response.isSuccessful && shouldUpdateToken) {
                val xAuthValues: HeaderValues = response.header("x-auth")
                val xAuth = xAuthValues.firstOrNull()
                if(xAuth != "") {
                    UserManager.sharedInstance().updateAuthToken(xAuth)
                    UtilsGeneral.log("Updated User Token = " + xAuth)
                }
            }

            val modelResponse = NetworkResponseDataModel()

            try {
                UtilsGeneral.log("response : " + String(response.data))
                val jsonObject = JSONObject(String(response.data))
                modelResponse.payload = jsonObject

                if(jsonObject.has("error")) {
                    modelResponse.errorMessage = UtilsString.parseString(jsonObject["error"])
                }
                if(jsonObject.has("message")) {
                    modelResponse.errorMessage = UtilsString.parseString(jsonObject["message"])
                }
                if(jsonObject.has("code")) {
                    modelResponse.errorCode = UtilsString.parseString(jsonObject["code"])
                }

                modelResponse.code = response.statusCode
            } catch (e : Exception) {
                UtilsGeneral.log("response: " + e.message.toString())
                return instanceForFailure()
            }

            return modelResponse
        }

        fun instanceForFailure(): NetworkResponseDataModel {
            val instance = NetworkResponseDataModel()
            instance.code = EnumNetworkResponseCode.CODE_400_BADREQUEST.value
            return instance
        }

        fun instanceForSuccess(): NetworkResponseDataModel {
            val instance = NetworkResponseDataModel()
            return instance
        }
    }
}

enum class EnumNetworkResponseCode(val value: Int) {

    CODE_200_OK(200),
    CODE_201_CREATED(201),
    CODE_400_BADREQUEST(400),
    CODE_401_UNAUTHORIZED(401),
    CODE_403_FORBIDDEN(403),
    CODE_404_NOTFOUND(404),
    CODE_500_SERVERERROR(500);
}