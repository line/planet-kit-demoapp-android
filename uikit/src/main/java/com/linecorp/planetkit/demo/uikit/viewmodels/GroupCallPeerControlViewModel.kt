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
import com.linecorp.planetkit.demo.uikit.usecases.GroupCallPeerControlUseCase
import com.linecorp.planetkit.ui.PlanetKitVideoView

class GroupCallPeerControlViewModel(private val groupCallPeerControlUseCase: GroupCallPeerControlUseCase): ViewModel() {
    private var isVideoStarted = false

    private val _isMuteOn = MutableLiveData<Boolean?>(null)
    val isMuteOn: LiveData<Boolean?>
        get() = _isMuteOn

    private val _videoStatus = MutableLiveData<UIKitVideoStatus?>(null)
    val videoStatus: LiveData<UIKitVideoStatus?>
        get() = _videoStatus

    private val _isSpeaking = MutableLiveData(false)
    val isSpeaking: LiveData<Boolean>
        get() = _isSpeaking

    private val listener = object: GroupCallPeerControlUseCase.Listener {
        override fun onMuteChanged(muteOn: Boolean) {
            _isMuteOn.postValue(muteOn)
        }

        override fun onVideoStatusChanged(videoStatus: UIKitVideoStatus) {
            _videoStatus.postValue(videoStatus)
            if (videoStatus != UIKitVideoStatus.DISABLED && isVideoStarted) {
                startVideo()
            }
        }

        override fun onAverageVolumeLevelChanged(level: Int) {
            _isSpeaking.postValue(level > Constants.SPEAKING_VOLUME_LEVEL)
        }
    }

    fun setPeer(peer: UIKitUser, videoView: PlanetKitVideoView?) {
        groupCallPeerControlUseCase.init(peer, videoView, listener)
    }

    fun clearPeer() {
        groupCallPeerControlUseCase.onCleared()
    }

    fun startVideo() {
        groupCallPeerControlUseCase.startVideo()
        isVideoStarted = true
    }

    fun stopVideo() {
        groupCallPeerControlUseCase.stopVideo()
        isVideoStarted = false
    }

    override fun onCleared() {
        super.onCleared()
        groupCallPeerControlUseCase.onCleared()
        _isMuteOn.postValue(null)
        _videoStatus.postValue(null)
        _isSpeaking.postValue(false)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).appContainer.groupCallRepository
                GroupCallPeerControlViewModel(GroupCallPeerControlUseCase(repository))
            }
        }
    }
}