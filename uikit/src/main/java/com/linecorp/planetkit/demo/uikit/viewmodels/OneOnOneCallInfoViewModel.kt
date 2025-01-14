package com.linecorp.planetkit.demo.uikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneCallInfoUseCase
import java.util.Timer
import java.util.TimerTask

class OneOnOneCallInfoViewModel(private val callInfoUseCase: OneOnOneCallInfoUseCase): ViewModel() {
    val peerId: String?
        get() = callInfoUseCase.peerId

    val isVoiceCall: Boolean
        get() = callInfoUseCase.isVoiceCall

    private val _durationString = MutableLiveData("00:00:00")
    val durationString: LiveData<String>
        get() = _durationString

    private val timerTask = object: TimerTask() {
        override fun run() {
            callInfoUseCase.durationMilliseconds?.let { it ->
                val durationSec = it / 1000
                val hours = durationSec / 3600
                val minutes = (durationSec % 3600) / 60
                val secs = durationSec % 60
                _durationString.postValue(String.format("%02d:%02d:%02d", hours, minutes, secs))
            }
        }
    }

    init {
        Timer().schedule(timerTask, 1000, 1000)
    }

    override fun onCleared() {
        super.onCleared()
        timerTask.cancel()
    }

    companion object {
        fun Factory(callInstanceId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).
                appContainer.callRepositoryContainer.getRepository(callInstanceId)
                    ?: throw Exception("Repository is null")
                OneOnOneCallInfoViewModel(OneOnOneCallInfoUseCase(repository))
            }
        }
    }
}