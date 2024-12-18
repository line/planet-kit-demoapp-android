package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository
import java.util.Date

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
        val registerResultStr = appServerRepository.registerUserV2(userId, userName, notificationType) ?: return
        appServerRepository.registerDeviceV2(registerResultStr)
        appServerRepository.updateNotificationTokenV2(registerResultStr, notificationType,
            appServerRepository.getCurrentNotificationToken(notificationType), null, true)

        serviceLocatorRepository.apply {
            this.userName = userName
            this.userId = userId
            this.appServerAuth = registerResultStr
        }
    }
}