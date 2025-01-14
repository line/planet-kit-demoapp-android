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
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneCallPeerControlUseCase
import com.linecorp.planetkit.ui.PlanetKitVideoView

class OneOnOneCallPeerControlViewModel(
    private val peerControlUseCase: OneOnOneCallPeerControlUseCase
): ViewModel() {
    private val _isMuteOn = MutableLiveData(false)
    val isMuteOn: LiveData<Boolean>
        get() = _isMuteOn

    private val _isSpeaking = MutableLiveData(false)
    val isSpeaking: LiveData<Boolean>
        get() = _isSpeaking


    private val _videoStatus = MutableLiveData<UIKitVideoStatus?>(null)
    val videoStatus: LiveData<UIKitVideoStatus?>
        get() = _videoStatus

    private val listener = object: OneOnOneCallPeerControlUseCase.Listener {
        override fun onMuteChanged(muteOn: Boolean) {
            _isMuteOn.postValue(muteOn)
        }

        override fun onVideoStatusChanged(videoStatus: UIKitVideoStatus) {
            _videoStatus.postValue(videoStatus)
        }

        override fun onAverageVolumeLevelChanged(volume: Int) {
            _isSpeaking.postValue(volume > SPEAKING_VOLUME_LEVEL)
        }
    }

    fun addPeerVideoView(peerView: PlanetKitVideoView) {
        peerControlUseCase.addPeerVideoView(peerView)
    }

    fun removePeerVideoView(peerView: PlanetKitVideoView) {
        peerControlUseCase.removePeerVideoView(peerView)
    }

    override fun onCleared() {
        super.onCleared()
        _isMuteOn.postValue(false)
        _videoStatus.postValue(null)
        peerControlUseCase.onCleared()
    }

    init {
        peerControlUseCase.setListener(listener)
    }

    companion object {
        private const val SPEAKING_VOLUME_LEVEL = 5

        fun Factory(callInstanceId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).
                appContainer.callRepositoryContainer.getRepository(callInstanceId)
                    ?: throw Exception("Repository is null")
                OneOnOneCallPeerControlViewModel(OneOnOneCallPeerControlUseCase(repository))
            }
        }
    }
}