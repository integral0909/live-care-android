package com.onseen.livecare.models.Communication

import android.net.Network
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.jsonBody
import org.json.JSONObject

import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsGeneral
import com.onseen.livecare.models.Utils.UtilsString
import java.io.File
import java.io.IOException
import java.nio.charset.Charset


class NetworkManager {

    companion object {

        fun GET(endpoint: String, params: JSONObject?, authOptions: Int, callback:NetworkManagerResponse) {
            var urlString = endpoint
            if(params != null) {
                urlString = urlString + "?"
                val keys = params.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value: Any = params[key]
                    urlString = urlString + key + "=" + value + "&"
                }
            }

            if(urlString.last().equals("&")) urlString = urlString.substring(0, urlString.length - 2)

            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - GET: " + urlString)

            val fuelRequest = Fuel.get(urlString)
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/jsoon")

            if ((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
            }

            fuelRequest.response { _, response, _ ->

                val result = NetworkResponseDataModel.instanceFromDataResponse(response, ((authOptions and EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value) > 0))
                if (result.isSuccess()) {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - succeeded")
                } else {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Failed")
                }

                callback.onComplete(result)
            }
        }

        fun POST(urlString: String, params: JSONObject?, authOptions: Int, callback: NetworkManagerResponse) {
            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - POST: " + urlString)

            val fuelRequest = Fuel.post(urlString)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .jsonBody(UtilsString.getStringForDictionary(params))

            if ((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
            }

            fuelRequest.response() { _, response, _ ->

                val result = NetworkResponseDataModel.instanceFromDataResponse(response, ((authOptions and EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value) > 0))
                if (result.isSuccess()) {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - succeeded")
                } else {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Failed")
                }

                callback.onComplete(result)
            }
        }

        fun PUT(urlString: String, params: JSONObject?, authOptions: Int, callback: NetworkManagerResponse) {
            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - PUT: " + urlString)

            val strParams = UtilsString.getStringForDictionary(params)
            val fuelRequest = Fuel.put(urlString)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .jsonBody(strParams, Charset.forName("utf-8"))

            if ((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
            }

            fuelRequest.response() { _, response, _ ->

                val result = NetworkResponseDataModel.instanceFromDataResponse(response, ((authOptions and EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value) > 0))
                if (result.isSuccess()) {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - succeeded")
                } else {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Failed")
                }

                callback.onComplete(result)
            }
        }

        fun PATCH(urlString: String, params: JSONObject?, authOptions: Int, callback: NetworkManagerResponse) {
            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - PATCH: " + urlString)

            val fuelRequest = Fuel.patch(urlString)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .jsonBody(UtilsString.getStringForDictionary(params))

            if ((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
            }

            fuelRequest.response() { _, response, _ ->

                val result = NetworkResponseDataModel.instanceFromDataResponse(response, ((authOptions and EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value) > 0))
                if (result.isSuccess()) {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - succeeded")
                } else {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Failed")
                }

                callback.onComplete(result)
            }
        }

        fun DELETE(urlString: String, params: JSONObject?, authOptions: Int, callback: NetworkManagerResponse) {
            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - DELETE: " + urlString)

            val fuelRequest = Fuel.delete(urlString)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .jsonBody(UtilsString.getStringForDictionary(params))

            if ((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
            }

            fuelRequest.response() { _, response, _ ->

                val result = NetworkResponseDataModel.instanceFromDataResponse(response, ((authOptions and EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value) > 0))
                if (result.isSuccess()) {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - succeeded")
                } else {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Failed")
                }

                callback.onComplete(result)
            }
        }

        fun UPLOAD(endPoint: String, fileName: String, mimeType: String, file: File, authOptions: Int, callback: NetworkManagerResponse) {
            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - UPLOAD FILE: " + endPoint)

            val fuelRequest = Fuel.upload(endPoint)
                .add(
                    FileDataPart(file, name = "file", filename=fileName)
                )


            if((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
                    .header("Content_type", "multipart/form-data")
            }

            fuelRequest.response { _, response, _ ->
                val result = NetworkResponseDataModel.instanceFromDataResponse(response, ((authOptions and EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value) > 0))
                if(result.isSuccess()) {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Succeeded")
                } else {
                    UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - Failed. Reason = " + response.responseMessage)
                }
                callback.onComplete(result)
            }
        }

        fun DOWNLOAD(endpoint: String, mimeType: String, authOptions: Int, callback: NetworkManagerResponse) {
            val sessionToken = UtilsString.generateRandomString(16)
            UtilsGeneral.log("[NetworkManager - (" + sessionToken + ")] - DOWNLOAD FILE: " + endpoint)

            val fuelRequest = Fuel.download(endpoint)
                .fileDestination { response, url ->
                    File.createTempFile("temp", ".tmp")
                }
                .requestProgress { readBytes, totalBytes ->
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                    println("Bytes downloaded $readBytes / $totalBytes ($progress %)")
                }

            if((authOptions and EnumNetworkAuthOptions.AUTH_REQUIRED.value) > 0 && UserManager.sharedInstance().isLoggedIn()) {
                fuelRequest.header("x-auth", UserManager.sharedInstance().getAuthToken())
            }

            fuelRequest.response { _, response, _ ->
                val result = NetworkResponseDataModel()
                if(response.statusCode == EnumNetworkResponseCode.CODE_200_OK.value) {
                    result.code = response.statusCode
                    result.parsedObject = response.url.toString()
                } else {
                    result.code = EnumNetworkResponseCode.CODE_400_BADREQUEST.value
                    result.errorMessage = "Unkown Error"
                }

                callback.onComplete(result)
            }

        }
    }
}

enum class EnumNetworkAuthOptions(val value: Int) {
    AUTH_NONE(0b00000000),
    AUTH_REQUIRED(0b00000001),
    AUTH_SHOULDUPDATE(0b00000010);
}