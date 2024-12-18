package com.linecorp.planetkit.demo.uikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.consts.Constants
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitUser
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitVideoStatus
import com.linecorp.planetkit.demo.uikit.usecases.GroupCallMyStatusUseCase
import com.linecorp.planetkit.ui.PlanetKitVideoView

class GroupCallMyStatusViewModel(private val groupCallMyStatusUseCase: GroupCallMyStatusUseCase): ViewModel() {

    private val _videoStatus = MutableLiveData<UIKitVideoStatus?>(null)
    val videoStatus: LiveData<UIKitVideoStatus?>
        get() = _videoStatus

    private val _isMuteOn = MutableLiveData(false)
    val isMuteOn: LiveData<Boolean>
        get() = _isMuteOn

    private val _isSpeaking = MutableLiveData(false)
    val isSpeaking: LiveData<Boolean>
        get() = _isSpeaking

    val me: UIKitUser?
        get() = groupCallMyStatusUseCase.me

    private val listener = object: GroupCallMyStatusUseCase.Listener {

        override fun onMuteStateChanged(on: Boolean) {
            _isMuteOn.postValue(on)
        }

        override fun onVideoStatusUpdated(videoStatus: UIKitVideoStatus) {
            _videoStatus.postValue(videoStatus)
        }

        override fun onAverageAudioVolumeLevel(averageVolumeLevel: Int) {
            _isSpeaking.postValue(averageVolumeLevel > Constants.SPEAKING_VOLUME_LEVEL && !groupCallMyStatusUseCase.isMuteOn)
        }
    }

    init {
        groupCallMyStatusUseCase.setListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        groupCallMyStatusUseCase.onCleared()
    }

    fun addVideo(videoView: PlanetKitVideoView) {
        groupCallMyStatusUseCase.addVideoView(videoView)
    }

    fun removeVideo(videoView: PlanetKitVideoView) {
        groupCallMyStatusUseCase.removeVideoView(videoView)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).appContainer.groupCallRepository
                GroupCallMyStatusViewModel(GroupCallMyStatusUseCase(planetKitConferenceRepository = repository))
            }
        }
    }

    fun muteOn(on: Boolean) {
        groupCallMyStatusUseCase.muteOn(on)
    }

    fun pauseVideo() {
        groupCallMyStatusUseCase.pauseVideo()
    }

    fun resumeVideo() {
        groupCallMyStatusUseCase.resumeVideo()
    }
}