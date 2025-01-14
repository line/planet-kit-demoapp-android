package com.linecorp.planetkit.demo.uikit.repositories

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.linecorp.planetkit.PlanetKit
import com.linecorp.planetkit.demo.uikit.utils.JwtUtil
import org.json.JSONObject
import java.util.Date

class ServiceLocatorRepository(
    private val context: Context,
    val serviceId: String,
    private val endToneResId: Int? = null,
    private val holdToneResId: Int? = null,
    private val ringBackToneResId: Int? = null,
    private val ringToneResId: Int? = null,
) {
    private val preference: Preference by lazy {
        Preference(context)
    }

    val endToneUri: Uri?
        get() = getToneUri(endToneResId)

    val holdToneUri: Uri?
        get() = getToneUri(holdToneResId)

    val ringBackToneUri: Uri?
        get() = getToneUri(ringBackToneResId)

    val ringToneUri: Uri?
        get() = getToneUri(ringToneResId)

    var userName: String
        set(value) = preference.putString(Preference.KEY_USER_NAME, value)
        get() = preference.getString(Preference.KEY_USER_NAME, "").toString()

    var userId: String
        set(value) = preference.putString(Preference.KEY_USER_ID, value)
        get() = preference.getString(Preference.KEY_USER_ID, "").toString()

    var appServerAuth: String
        set(value) = preference.putString(Preference.KEY_AS_AUTH, value)
        get() = preference.getString(Preference.KEY_AS_AUTH, "").toString()

    val expireDate: Date?
        get() {
            if (appServerAuth.isNotEmpty()) {
                val str = JwtUtil.decodeJwt(appServerAuth)[JwtUtil.Type.PAYLOAD] as String
                val expLong = JSONObject(str).optLong(AppServerRepository.EXPIRE_TIME_KEY, -1)
                if (expLong.toInt() == -1) {
                    return null
                }
                return Date(expLong * 1000)
            }
            return null
        }

    val sdkVersionName: String by lazy {
        PlanetKit.version
    }

    private fun getToneUri(resId: Int?): Uri? {
        if (resId != null) {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + resId)
        }
        return null
    }


}