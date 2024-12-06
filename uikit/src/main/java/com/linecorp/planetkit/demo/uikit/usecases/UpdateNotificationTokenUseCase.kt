package com.linecorp.planetkit.demo.uikit.usecases

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UpdateNotificationTokenUseCase(
    private val appServerRepository: AppServerRepository,
    private val serviceLocatorRepository: ServiceLocatorRepository) {

    suspend fun silentRegisterToken(newToken: String? = null, notificationType: String) = withContext(Dispatchers.IO) {
        val userId = serviceLocatorRepository.userId
        if (userId.isEmpty()) {
            return@withContext
        }

        val token = newToken ?: getCurrentNotificationToken()
        appServerRepository.updateNotificationTokenV2(serviceLocatorRepository.appServerAuth, notificationType, token, null, false)
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