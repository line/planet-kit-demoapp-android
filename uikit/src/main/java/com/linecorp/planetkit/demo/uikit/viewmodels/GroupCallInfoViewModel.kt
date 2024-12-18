package com.linecorp.planetkit.demo.uikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.usecases.GroupCallInfoUseCase
import java.util.Timer
import java.util.TimerTask

class GroupCallInfoViewModel(private val groupCallInfoUseCase: GroupCallInfoUseCase): ViewModel() {
    val roomName: String?
        get() = groupCallInfoUseCase.roomName

    private var durationSeconds = 0

    private val _durationString = MutableLiveData("00:00:00")
    val durationString: LiveData<String>
        get() = _durationString

    private val timerTask = object: TimerTask() {
        override fun run() {
            ++durationSeconds
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val secs = durationSeconds % 60

            _durationString.postValue(String.format("%02d:%02d:%02d", hours, minutes, secs))
        }
    }
    init {
        groupCallInfoUseCase.durationMilliseconds?.let {
            durationSeconds = it / 1000
        }
        Timer().schedule(timerTask, 1000, 1000)
    }

    override fun onCleared() {
        super.onCleared()
        timerTask.cancel()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).appContainer.groupCallRepository
                GroupCallInfoViewModel(GroupCallInfoUseCase(planetKitConferenceRepository = repository))
            }
        }
    }
}