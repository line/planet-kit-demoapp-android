package com.linecorp.planetkit.demo.uikit.repositories

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Preference(context: Context) {
    fun putString(key: String, defaultValue: String?) {
        editor.putString(key, defaultValue)
        editor.commit()
    }

    fun getString(key: String, defaultValue: String?): String? {
        return sharedPreference.getString(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreference.getInt(key, defaultValue)
    }

    fun putBoolean(key: String, defaultValue: Boolean) {
        editor.putBoolean(key, defaultValue)
        editor.commit()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreference.getBoolean(key, defaultValue)
    }

    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private val editor: SharedPreferences.Editor by lazy {
        sharedPreference.edit()
    }

    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_USER_NAME = "KEY_USER_NAME"
        const val KEY_AS_AUTH = "KEY_AS_AUTH"
        const val KEY_LATEST_UPDATED_TOKEN = "KEY_LATEST_UPDATED_TOKEN"
    }
}