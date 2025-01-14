package com.linecorp.planetkit.demo.uikit.usecases

import android.content.Context
import com.linecorp.planetkit.demo.uikit.model.OneOnOneCallStateModel
import com.linecorp.planetkit.demo.uikit.repositories.PlanetKitOneOnOneCallRepository
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitOneOnOneCallState

class OneOnOneCallStateUseCase(
    private val callRepository: PlanetKitOneOnOneCallRepository
): UseCase {
    val callState: UIKitOneOnOneCallState
        get() = UIKitOneOnOneCallState.transToUIKitState(callRepository.callStateModel.state)

    fun endCall() {
        callRepository.endCall()
    }

    fun acceptCall() {
        callRepository.acceptCall()
    }

    fun addStateListener(listener: Listener) {
        kitListenerList.add(listener)
    }

    fun removeStateListener(listener: Listener) {
        kitListenerList.remove(listener)
    }

    private val kitListenerList = ArrayList<Listener>()
    interface Listener {
        fun onWaitAnswer()
        fun onConnected(context: Context)
        fun onDisconnected(context: Context, reason: String)
    }

    private val listener = object: OneOnOneCallStateModel.Listener {
        override fun onWaitAnswer() {
            kitListenerList.forEach { it.onWaitAnswer() }
        }

        override fun onConnected(context: Context) {
            kitListenerList.forEach { it.onConnected(context) }
        }

        override fun onDisconnected(context: Context, reason: String) {
            kitListenerList.forEach { it.onDisconnected(context, reason) }
        }
    }

    init {
        callRepository.callStateModel.addStateListener(listener)
    }

    override fun onCleared() {
        kitListenerList.clear()
        callRepository.callStateModel.removeStateListener(listener)
    }
}