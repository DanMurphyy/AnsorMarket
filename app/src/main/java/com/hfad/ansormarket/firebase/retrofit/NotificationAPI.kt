package com.hfad.ansorowner.firebase.retrofit

import com.hfad.ansormarket.firebase.retrofit.PushNotification
import com.hfad.ansormarket.models.Constants.CONTENT_TYPE
import com.hfad.ansormarket.models.Constants.FCM_SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$FCM_SERVER_KEY", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}