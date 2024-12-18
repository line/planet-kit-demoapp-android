package com.linecorp.planetkit.demo.uikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneCallMyStatusUseCase
import com.linecorp.planetkit.ui.PlanetKitVideoView

class OneOnOneCallMyStatusViewModel(
    private val callMyStatusUseCase: OneOnOneCallMyStatusUseCase
): ViewModel() {
    private val _videoStatus = MutableLiveData<UIKitVideoStatus?>(null)
    val videoStatus: LiveData<UIKitVideoStatus?>
        get() = _videoStatus

    private val _isMuteOn = MutableLiveData(false)
    val isMuteOn: LiveData<Boolean>
        get() = _isMuteOn

    private val listener = object: OneOnOneCallMyStatusUseCase.Listener {

        override fun onMuteStateChanged(on: Boolean) {
            _isMuteOn.postValue(on)
        }

        override fun onVideoStatusUpdated(videoStatus: UIKitVideoStatus) {
            _videoStatus.postValue(videoStatus)
        }
    }

    init {
        callMyStatusUseCase.setListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        callMyStatusUseCase.onCleared()
    }

    fun addMyVideoView(videoView: PlanetKitVideoView) {
        callMyStatusUseCase.addMyVideoView(videoView)
    }

    fun removeVideoView(videoView: PlanetKitVideoView) {
        callMyStatusUseCase.removeVideoView(videoView)
    }

    fun muteOn(on: Boolean) {
        _isMuteOn.postValue(on)
        callMyStatusUseCase.muteOn(on)
    }

    fun pauseVideo() {
        callMyStatusUseCase.pauseVideo()
    }

    fun resumeVideo() {
        callMyStatusUseCase.resumeVideo()
    }

    companion object {
        fun Factory(callInstanceId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).
                appContainer.callRepositoryContainer.getRepository(callInstanceId)
                    ?: throw Exception("Repository is null")
                OneOnOneCallMyStatusViewModel(OneOnOneCallMyStatusUseCase(repository))
            }
        }
    }
}