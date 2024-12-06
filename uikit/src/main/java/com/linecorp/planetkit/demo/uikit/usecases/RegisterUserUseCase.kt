package com.linecorp.planetkit.demo.uikit.usecases

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository
import com.linecorp.planetkit.demo.uikit.utils.JwtUtil
import org.json.JSONObject
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RegisterUserUseCase(
    private val serviceLocatorRepository: ServiceLocatorRepository,
    private val appServerRepository: AppServerRepository
) {
    val userName
        get() = serviceLocatorRepository.userName

    val userId
        get() = serviceLocatorRepository.userId

    val expDate
        get() = serviceLocatorRepository.expireDate

    val isRegistered: Boolean
        get() {
            if (userId.isEmpty() || userName.isEmpty() || serviceLocatorRepository.serviceId.isEmpty()) {
                return false
            }
            expDate?.let {
                val currentDate = Date()
                if (it.after(currentDate)) {
                    return true
                } else {
                    resetUser()
                    return false
                }
            }
            return true
        }

    fun resetUser() {
        serviceLocatorRepository.userId = ""
        serviceLocatorRepository.userName = ""
        serviceLocatorRepository.appServerAuth = ""
    }
    suspend fun registerUser(userName: String, userId: String, notificationType: String) {
        val registerResultStr = appServerRepository.registerUserV2(userId, userName) ?: return
        appServerRepository.registerDeviceV2(registerResultStr)
        appServerRepository.updateNotificationTokenV2(registerResultStr, notificationType, getCurrentNotificationToken(), null, true)

        serviceLocatorRepository.apply {
            this.userName = userName
            this.userId = userId
            this.appServerAuth = registerResultStr
        }
    }

    private suspend fun getCurrentNotificationToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance()
            .token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    continuation.resumeWithException(RuntimeException("Cannot retrieve current token"))
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.toString()

                if (token == null) {
                    continuation.resumeWithException(RuntimeException("Cannot retrieve current token"))
                    return@OnCompleteListener
                }

                continuation.resume(token)
            })
    }
}