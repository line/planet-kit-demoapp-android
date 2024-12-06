package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitRepository

class GetSDKVersionUseCase(
    private val kitRepository: PlanetKitRepository
) {
    val sdkVersion: String
        get() = kitRepository.sdkVersion
}