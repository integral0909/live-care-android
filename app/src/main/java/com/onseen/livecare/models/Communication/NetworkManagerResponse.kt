package com.onseen.livecare.models.Communication

interface  NetworkManagerResponse {
    fun onComplete(responseDataModel: NetworkResponseDataModel)
}