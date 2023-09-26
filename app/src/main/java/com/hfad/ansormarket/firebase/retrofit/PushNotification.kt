package com.hfad.ansormarket.firebase.retrofit

import com.hfad.ansormarket.firebase.retrofit.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String
)