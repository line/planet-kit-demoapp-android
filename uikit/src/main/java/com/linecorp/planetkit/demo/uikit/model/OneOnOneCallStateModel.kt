package com.linecorp.planetkit.demo.uikit.model

import android.content.Context
import com.linecorp.planetkit.session.call.PlanetKitCallState

class OneOnOneCallStateModel {
    private var _state: PlanetKitCallState = PlanetKitCallState.IDLE

    private val listenerList = ArrayList<Listener>()

    val state
        get() = _state

    internal fun update(context: Context, state: PlanetKitCallState, extraData: String? = null) {
        _state = state
        when(state) {
            PlanetKitCallState.CONNECTED -> {
                listenerList.forEach { it.onConnected(context) }
            }
            PlanetKitCallState.END -> {
                listenerList.forEach { it.onDisconnected(context, extraData!!) }
            }
            PlanetKitCallState.WAITANSWER -> {
                listenerList.forEach { it.onWaitAnswer() }
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
        fun onWaitAnswer()
        fun onConnected(context: Context)
        fun onDisconnected(context: Context, reason: String)
    }
}