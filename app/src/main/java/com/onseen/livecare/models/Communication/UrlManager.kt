package com.onseen.livecare.models.Communication

import com.onseen.livecare.models.Consumer.DataModel.ConsumerDataModel
import com.onseen.livecare.models.Utils.UtilsGeneral
import java.util.*

class UrlManager {

    class UserApi {
        companion object {
            /*************** Global: User ****************/

            fun login(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/login")
            }

            fun signup(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/sign-up")
            }

            fun updateMyProfile(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() +"/users/me")
            }

            fun getMyProfile(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/me")
            }

            fun forgotPassword(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/forget-password")
            }
        }
    }

    /*************** Global: Invites ****************/

    class InviteApi {
        companion object {
            fun getInvites(userId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/" + userId + "/invites")
            }

            fun acceptInvite(userId: String, token: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/" + userId + "/invites/" + token + "/accept")
            }

            fun declineInvite(userId: String, token: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/users/" + userId + "/invites/" + token + "/decline")
            }
        }
    }

    /*************** Global: Consumers ****************/
    class ConsumerApi {
        companion object {
            fun getConsumers(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/consumers")
            }

            fun getOrganizationConsumers(organizationId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/" + organizationId + "/consumers")
            }
        }
    }

    /*************** Global: Financial Accounts ****************/
    class FinancialAccountApi {
        companion object {
            fun createAccount(organizationId: String, consumerId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() +  "/organizations/" + organizationId + "/consumers/" + consumerId + "/accounts")
            }

            fun audit(organizationId: String, consumerId: String, accountId: String) : String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/consumers/"+ consumerId + "/accounts/" + accountId +  "/audits")
            }

            fun getFinancialAccounts(organizationId: String, consumerId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/consumers/" + consumerId + "/accounts")
            }

            fun uploadMediaForFinancialAccount(organizationId: String, consumerId: String, accountId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/consumers/" + consumerId + "/accounts/" + accountId + "/media")
            }
        }
    }

    /*************** Global: Transactions ****************/

    class TransactionApi {
        companion object {
            fun getTransactions(): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/transactions")
            }

            fun createTransaction(organizationId: String, consumerId: String, accountId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/consumers/" + consumerId + "/accounts/" + accountId + "/transactions")
            }

            fun updateTransaction(organizationId: String, consumerId: String, accountId: String, transactionId: String) : String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/consumers/" + consumerId + "/accounts/" + accountId + "/transactions/" + transactionId)
            }
        }
    }

    /*************** Global: Trip Requests ****************/

    class TripRequestApi {
        companion object {
            fun getTripRequestsByOrganizationId(organizationId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/requests")
            }

            fun createTripRequest(organizationId: String, consumerId: String): String {
                return String.format(Locale.US, UtilsGeneral.getApiBaseUrl() + "/organizations/" + organizationId + "/consumers/" + consumerId + "/requests")
            }
        }
    }
}