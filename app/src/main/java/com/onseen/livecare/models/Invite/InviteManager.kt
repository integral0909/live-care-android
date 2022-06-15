package com.onseen.livecare.models.Invite

import com.onseen.livecare.models.AppManager.AppManager
import com.onseen.livecare.models.Communication.*
import com.onseen.livecare.models.Invite.DataModel.EnumInvitationStatus
import com.onseen.livecare.models.Invite.DataModel.InviteDataModel
import com.onseen.livecare.models.LocalNotification.LocalNotificationManager
import com.onseen.livecare.models.User.UserManager
import com.onseen.livecare.models.Utils.UtilsGeneral.Companion.INVITE_LISTUPDATED

class InviteManager {

    companion object {
        private val sharedInstance: InviteManager = InviteManager()

        @Synchronized
        fun sharedInstance(): InviteManager {
            return sharedInstance
        }
    }

    var arrayInvites: ArrayList<InviteDataModel> = ArrayList()

    constructor() {
        initialize()
    }

    fun initialize() {
        this.arrayInvites = ArrayList()
    }

    // MARK Utils
    fun addInviteIfNeeded(newInvite: InviteDataModel) {
        if(!newInvite.isValid()) {
            return
        }

        for(invite in this.arrayInvites) {
            if(invite.id.equals(newInvite.id)) return
        }

        this.arrayInvites.add(newInvite)
    }

    fun getPendingInvites(): ArrayList<InviteDataModel> {
        val array: ArrayList<InviteDataModel> = ArrayList()
        for(invite in this.arrayInvites) {
            if(invite.enumStatus == EnumInvitationStatus.PENDING)
                array.add(invite)
        }

        return array
    }

    fun requestGetInvites(callback: NetworkManagerResponse?) {
        if(UserManager.sharedInstance().currentUser == null) {
            callback?.onComplete(NetworkResponseDataModel.instanceForFailure())
            return
        }

        val urlString = UrlManager.InviteApi.getInvites(UserManager.sharedInstance().currentUser!!.id!!)
        NetworkManager.GET(urlString, null, EnumNetworkAuthOptions.AUTH_REQUIRED.value, object : NetworkManagerResponse{
            override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                if(responseDataModel.isSuccess()) {
                    if(responseDataModel.payload.has("data") && !responseDataModel.payload.isNull("data")) {
                        val array = responseDataModel.payload.getJSONArray("data")
                        for(i in 0 until array.length()) {
                            val dict = array.getJSONObject(i)
                            val invite = InviteDataModel()
                            invite.deserialize(dict)
                            if(invite.isValid())
                                addInviteIfNeeded(invite)
                        }
                    }

                    LocalNotificationManager.sharedInstance().notifyLocalNotification(INVITE_LISTUPDATED)
                }
                callback?.onComplete(responseDataModel)
            }
        })
    }

    fun requestAcceptInvite(invite: InviteDataModel, callback: NetworkManagerResponse) {

        val userId = UserManager.sharedInstance().currentUser?.id
        if(userId!!.isEmpty()) {
            callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            return
        }

        val urlString = UrlManager.InviteApi.acceptInvite(userId, invite.token)

        NetworkManager.POST(urlString,
            null,
            EnumNetworkAuthOptions.AUTH_REQUIRED.value or EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value,
            object : NetworkManagerResponse {
                override fun onComplete(responseDataModel0: NetworkResponseDataModel) {
                    if(responseDataModel0.isSuccess()) {
                        invite.enumStatus = EnumInvitationStatus.ACCEPTED
                        UserManager.sharedInstance().requestGetMyProfile(object : NetworkManagerResponse {
                            override fun onComplete(responseDataModel1: NetworkResponseDataModel) {
                                callback.onComplete(responseDataModel1)
                                AppManager.sharedInstance().initializeManagersAfterInvitationAccepted()
                            }
                        })
                    } else {
                        callback.onComplete(responseDataModel0)
                    }
                }
            })
    }

    fun requestDeclineInvite(invite: InviteDataModel, callback: NetworkManagerResponse) {

        val userId = UserManager.sharedInstance().currentUser?.id
        if(userId!!.isEmpty()) {
            callback.onComplete(NetworkResponseDataModel.instanceForFailure())
            return
        }

        val urlString = UrlManager.InviteApi.declineInvite(userId, invite.token)

        NetworkManager.POST(urlString,
            null,
            EnumNetworkAuthOptions.AUTH_REQUIRED.value or EnumNetworkAuthOptions.AUTH_SHOULDUPDATE.value,
            object : NetworkManagerResponse {
                override fun onComplete(responseDataModel: NetworkResponseDataModel) {
                    if(responseDataModel.isSuccess()) {
                        invite.enumStatus = EnumInvitationStatus.DECLINED
                    }
                    callback.onComplete(responseDataModel)
                }
            })
    }
}