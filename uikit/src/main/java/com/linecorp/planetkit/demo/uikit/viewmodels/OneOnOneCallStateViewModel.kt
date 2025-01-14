package com.linecorp.planetkit.demo.uikit.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.NotificationService
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitOneOnOneCallState
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneCallStateUseCase

class OneOnOneCallStateViewModel(private val callStateUseCase: OneOnOneCallStateUseCase): ViewModel() {

    private val _callState = MutableLiveData<UIKitOneOnOneCallState?>(null)
    val callState: LiveData<UIKitOneOnOneCallState?>
        get() = _callState

    private var _disconnectReason = ""
    val disconnectReason: String
        get() = _disconnectReason

    init {
        _callState.postValue(callStateUseCase.callState)
        callStateUseCase.addStateListener(object : OneOnOneCallStateUseCase.Listener {
            override fun onWaitAnswer() {
                _callState.postValue(UIKitOneOnOneCallState.WAIT_ANSWER)
            }

            override fun onConnected(context: Context) {
                _callState.postValue(UIKitOneOnOneCallState.CONNECTED)
            }

            override fun onDisconnected(context: Context, reason: String) {
                clearNotification(context)
                _callState.postValue(UIKitOneOnOneCallState.DISCONNECTED)
                _disconnectReason = reason
            }
        })
    }

    private fun clearNotification(context: Context) {
        NotificationService.clear(context)
    }

    override fun onCleared() {
        super.onCleared()
        callStateUseCase.onCleared()
    }

    fun endCall() {
        callStateUseCase.endCall()
    }

    fun acceptCall() {
        callStateUseCase.acceptCall()
    }

    companion object {
        fun Factory(callInstanceId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication)
                    .appContainer.callRepositoryContainer.getRepository(callInstanceId)
                    ?: throw Exception("Could not found call instance")
                OneOnOneCallStateViewModel(OneOnOneCallStateUseCase(repository))
            }
        }
    }
}