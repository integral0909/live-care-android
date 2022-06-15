package com.onseen.livecare.models.Utils

import com.onseen.livecare.BuildConfig

enum class EnumAppEnvironment(val value: Int) {
    SANDBOX(0),
    STAGING(1),
    PRODUCTION(2)
}

class UtilsGeneral {

    companion object {

        fun log(text: String) {

            if(UtilsConfig.shouldLogForDebugging) {
                println(" [livecare] : $text")
            } else {
                println(" [livecare] : $text")
            }
        }

        fun getApiBaseUrl(): String {
            return when {
                UtilsConfig.enumAppEnvironment == EnumAppEnvironment.SANDBOX -> "https://sandbox-livecare-api.onseen.com"
                UtilsConfig.enumAppEnvironment == EnumAppEnvironment.STAGING -> "https://staging-livecare-api.onseen.com"
                UtilsConfig.enumAppEnvironment == EnumAppEnvironment.PRODUCTION -> "https://livecare-api.onseen.com"
                else -> "https://sandbox-livecare-api.onseen.com"
            }

        }

        fun getAppVersionString(): String {
            var appVersion = BuildConfig.VERSION_NAME
//            appVersion = appVersion.substring(0, appVersion.length - 4)
            return appVersion
        }

        fun getAppBuildString(): String {
            return BuildConfig.VERSION_CODE.toString()
        }

        fun getBeautifiedAppVersionInfo(): String {
            return when {
                UtilsConfig.enumAppEnvironment === EnumAppEnvironment.SANDBOX -> "Version " + getAppVersionString() + " - DEV"
                UtilsConfig.enumAppEnvironment === EnumAppEnvironment.STAGING -> "Version " + getAppVersionString() + " - QA"
                UtilsConfig.enumAppEnvironment === EnumAppEnvironment.PRODUCTION -> "Version " + getAppVersionString()
                else -> "Version " + getAppVersionString() + " - DEV"
            }
        }

        val CONSUMERS_LISTUPDATED: String = "LiveCare.Consumers.ListUpdated"
        val TRANSACTIONS_LISTUPDATED: String = "LiveCare.Transactions.ListUpdated"
        val INVITE_LISTUPDATED: String = "LiveCare.invite.ListUpdated"

        val ARG_TAG = "param1"
        val ARG_TAB_INDEX = "param2"
    }
}