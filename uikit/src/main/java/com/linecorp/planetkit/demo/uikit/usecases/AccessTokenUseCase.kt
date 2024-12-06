package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.repositories.AppServerRepository
import com.linecorp.planetkit.demo.uikit.repositories.ServiceLocatorRepository

class AccessTokenUseCase(
    private val appServerRepository: AppServerRepository,
    private val serviceLocatorRepository: ServiceLocatorRepository
) {

    suspend fun getAccessToken(): String? {
        return appServerRepository.getAccessTokenV2(serviceLocatorRepository.appServerAuth)
    }
}