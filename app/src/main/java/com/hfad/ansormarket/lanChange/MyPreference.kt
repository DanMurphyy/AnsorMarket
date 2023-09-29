package com.hfad.ansormarket.lanChange

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

val PREFERENCE_NAME = "SharedPreferenceExample"
val PREFERENCE_LANGUAGE = "Language"

@Singleton
class MyPreference @Inject constructor( val context: Context) {
    val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLoginCount(): String? {
        return preference.getString(PREFERENCE_LANGUAGE, "en")
    }

    fun setLoginCount(Language: String) {
        val editor: SharedPreferences.Editor? = preference.edit()
        editor?.putString(PREFERENCE_LANGUAGE, Language)
        editor?.apply()
    }
}