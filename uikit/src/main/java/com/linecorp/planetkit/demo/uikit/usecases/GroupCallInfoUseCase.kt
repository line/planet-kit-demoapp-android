package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitGroupCallRepository

class GroupCallInfoUseCase(
    private val planetKitConferenceRepository: PlanetKitGroupCallRepository? = null
) {
    val roomName: String?
        get() = planetKitConferenceRepository?.roomName

    val durationMilliseconds: Int?
        get() = planetKitConferenceRepository?.durationMilliseconds
}