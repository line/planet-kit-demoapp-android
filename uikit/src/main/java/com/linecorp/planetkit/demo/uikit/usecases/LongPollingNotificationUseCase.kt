package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository
import com.linecorp.planetkit.demo.uikit.repositories.models.LongPollingResult
import java.util.Date

class LongPollingNotificationUseCase(private val appServerRepository: AppServerRepository,
                                     private val serviceLocatorRepository: ServiceLocatorRepository
) {
    val isLongPollingAvailable: Boolean
        get() {
            if (serviceLocatorRepository.appServerAuth.isEmpty()) {
                return false
            }
            serviceLocatorRepository.expireDate?.let {
                val currentDate = Date()
                return it.after(currentDate)
            }
            return false
        }
    suspend fun longPollingNotification() : LongPollingResult {
        return appServerRepository.longPollingNotification(serviceLocatorRepository.appServerAuth)
    }
}