package com.linecorp.planetkit.demo.uikit.usecases

import com.linecorp.planetkit.PlanetKitMediaType
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepository

class OneOnOneCallInfoUseCase(private val callRepository: PlanetKitOneOnOneCallRepository) {
    val peerId
        get() = callRepository.peerId
    val isVoiceCall: Boolean
        get() = callRepository.mediaType == PlanetKitMediaType.AUDIO
    val durationMilliseconds: Int?
        get() = callRepository.durationMilliseconds
}