package com.linecorp.planetkit.demo.uikit.uikitdata

import com.linecorp.planetkit.session.call.PlanetKitCallState

enum class UIKitOneOnOneCallState {
    IDLE,
    WAIT_ANSWER,
    CONNECTED,
    DISCONNECTED;

    internal companion object {
        fun transToUIKitState(state: PlanetKitCallState): UIKitOneOnOneCallState {
            return when (state) {
                PlanetKitCallState.IDLE -> IDLE
                PlanetKitCallState.CONNECTED -> CONNECTED

                PlanetKitCallState.TRYING,
                PlanetKitCallState.WAITANSWER -> WAIT_ANSWER

                PlanetKitCallState.TRY_ENDING,
                PlanetKitCallState.END-> DISCONNECTED
            }
        }
    }
}