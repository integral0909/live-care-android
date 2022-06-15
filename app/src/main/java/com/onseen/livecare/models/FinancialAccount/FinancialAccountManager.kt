package com.onseen.livecare.models.FinancialAccount

import com.onseen.livecare.models.Base.EnumMediaMimeType
import com.onseen.livecare.models.Base.MediaDataModel
import com.onseen.livecare.models.Communication.*
import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.FinancialAccount.DataModel.FinancialAccountDataModel
import com.onseen.livecare.models.Transaction.DataModel.AuditDataModel
import com.onseen.livecare.models.Utils.DispatchGroupManager
import com.onseen.livecare.models.Utils.UtilsGeneral
import com.onseen.livecare.models.Utils.UtilsString
import org.json.JSONArray
import java.io.File

class FinancialAccountManager {

    companion object {
        private val sharedInstance: FinancialAccountManager = FinancialAccountManager()

        @Synchronized
        fun sharedInstance(): FinancialAccountManager {
            return sharedInstance
        }
    }

    // MARK API Calls

    fun requestCreateAccount(account: FinancialAccountDataModel?, consumer: ConsumerDataModel?, callback: NetworkManagerResponse) {
        val urlString = UrlManager.FinancialAccountApi.createAccount(consumer!!.organizationId, consumer.id!!)
        NetworkManager.POST(urlString, account!!.serializeForCreate(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess())
                    requestGetAccountsForConsumer(consumer, true, callback)
                else
                    callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            }
        })
    }

    fun requestGetAccountsForConsumer(consumer: ConsumerDataModel, forceLoad: Boolean, callback: NetworkManagerResponse) {
        if(consumer.isFinancialAccountLoaded() && !forceLoad) {
            callback.onComplete(NetworkResponseDataModel.instanceForSuccess())
            return
        }

        val urlString: String = UrlManager.FinancialAccountApi.getFinancialAccounts(consumer.organizationId, consumer.id!!)
        NetworkManager.GET(urlString, null, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess() && responseDataModel.payload.has("data") && !responseDataModel.payload.isNull("data")) {
                    val data: JSONArray = responseDataModel.payload.getJSONArray("data")
                    val array: ArrayList<FinancialAccountDataModel> = ArrayList()

                    for(i in 0 until data.length()) {
                        val dict = data.getJSONObject(i)
                        val account = FinancialAccountDataModel()
                        account.deserialize(dict)
                        if(account.isValid()) {
                            array.add(account)
                        }
                    }
                    consumer.setAccountsWithSort(array)
                }

                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestAuditForAccount(audit: AuditDataModel, consumer: ConsumerDataModel, account: FinancialAccountDataModel, callback: NetworkManagerResponse) {
        val urlString = UrlManager.FinancialAccountApi.audit(consumer.organizationId, consumer.id!!, account.id!!)
        NetworkManager.POST(urlString, audit.serilizeForAudit(), EnumNetworkAuthOptions.AUTH_REQUIRED.value, object: NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestUploadPhotoForAccount(consumer: ConsumerDataModel, account: FinancialAccountDataModel, image: File, callback: NetworkManagerResponse) {
        val urlString = UrlManager.FinancialAccountApi.uploadMediaForFinancialAccount(consumer.organizationId, consumer.id!!, account.id!!)

        NetworkManager.UPLOAD(urlString, "file", EnumMediaMimeType.PNG.value, image, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse {
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    UtilsGeneral.log(responseDataModel.payload.toString())
                    val medium = MediaDataModel()
                    medium.deserialize(responseDataModel.payload)
                    responseDataModel.parsedObject = medium
                }

                callback.onComplete(responseDataModel)
            }
        })
    }

    fun requestUploadMultiplePhotosForAccount(consumer: ConsumerDataModel, account: FinancialAccountDataModel, images: ArrayList<File>, callback: NetworkManagerResponse) {
        val dgAsyncTaskGroup = DispatchGroupManager()
        val media: ArrayList<MediaDataModel> = ArrayList()

        for(image in images) {
            dgAsyncTaskGroup.enter()

            this.requestUploadPhotoForAccount(consumer, account, image, object : NetworkManagerResponse {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    if(responseDataModel.isSuccess() && responseDataModel.parsedObject != null) {
                        val medium = responseDataModel.parsedObject as MediaDataModel
                        media.add(medium)
                    }
                    dgAsyncTaskGroup.leave()
                }
            })
        }

        dgAsyncTaskGroup.notify( Runnable {
            val result = NetworkResponseDataModel()
            if(media.size == images.size) {
                result.code = EnumNetworkResponseCode.CODE_200_OK.value
            } else {
                result.code = EnumNetworkResponseCode.CODE_400_BADREQUEST.value
            }

            result.parsedObject = media
            callback.onComplete(result)
        })

    }
}