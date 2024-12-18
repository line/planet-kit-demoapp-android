package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateNotificationTokenUseCase(
    private val appServerRepository: AppServerRepository,
    private val serviceLocatorRepository: ServiceLocatorRepository) {

    suspend fun silentRegisterToken(newToken: String? = null, notificationType: String) = withContext(Dispatchers.IO) {
        val userId = serviceLocatorRepository.userId
        if (userId.isEmpty()) {
            return@withContext
        }

        val token = newToken ?: appServerRepository.getCurrentNotificationToken(notificationType)
        appServerRepository.updateNotificationTokenV2(serviceLocatorRepository.appServerAuth, notificationType, token, null, false)
    }

}