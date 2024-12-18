package com.linecorp.planetkit.demo.uikit.model

import android.content.Context
import com.linecorp.planetkit.session.conference.PlanetKitConferenceState

class GroupCallStateModel {
    private var _state: PlanetKitConferenceState = PlanetKitConferenceState.IDLE

    private val listenerList = ArrayList<Listener>()

    val isConnected
        get() = _state == PlanetKitConferenceState.CONNECTED

    internal fun update(context: Context, state: PlanetKitConferenceState, extraData: String? = null)
    {
        _state = state
        when(state) {
            PlanetKitConferenceState.CONNECTED -> {
                listenerList.forEach { it.onConnected(context) }
            }
            PlanetKitConferenceState.END -> {
                listenerList.forEach { it.onDisconnected(context, extraData!!) }
            }
            else -> {}
        }
    }

    fun addStateListener(listener: Listener) {
        listenerList.add(listener)
    }

    fun removeStateListener(listener: Listener) {
        listenerList.remove(listener)
    }

    interface Listener {
        fun onConnected(context: Context)
        fun onDisconnected(context: Context, reason: String)
    }
}