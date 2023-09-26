package com.hfad.ansormarket.firebase.retrofit

import com.hfad.ansormarket.models.Constants.BASE_URL
import com.hfad.ansorowner.firebase.retrofit.NotificationAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}