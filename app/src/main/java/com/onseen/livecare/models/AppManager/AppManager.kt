package com.onseen.livecare.models.AppManager

import com.onseen.livecare.models.Consumer.ConsumerManager
import com.onseen.livecare.models.Invite.InviteManager
import com.onseen.livecare.models.Transaction.TransactionManager
import com.onseen.livecare.models.User.UserManager

class AppManager {

    companion object {
        private val sharedInstance: AppManager = AppManager()

        @Synchronized
        fun sharedInstance(): AppManager {
            return sharedInstance
        }
    }

    fun initializeManagersAfterLanch() {

    }

    fun initializeManagersAfterLogin() {
        UserManager.sharedInstance().saveToLocalstorage()
        ConsumerManager.sharedInstance().requestGetConsumers(null)
        TransactionManager.sharedInstance().requestGetTransactions(null)
        InviteManager.sharedInstance().requestGetInvites(null)
    }

    fun initializeManagersAfterLogout() {
        ConsumerManager.sharedInstance().initialize()
        TransactionManager.sharedInstance().initialize()
        InviteManager.sharedInstance().initialize()
    }

    fun initializeManagersAfterInvitationAccepted() {
        ConsumerManager.sharedInstance().requestGetConsumers(null)
        TransactionManager.sharedInstance().requestGetTransactions(null)
    }

}