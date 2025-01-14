package com.linecorp.planetkit.demo.uikit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.usecases.CameraUseCase
import com.linecorp.planetkit.ui.PlanetKitVideoView

class CameraViewModel(private val cameraUseCase: CameraUseCase): ViewModel() {
    private val _isFrontCamera = MutableLiveData(true)
    val isFrontCamera: LiveData<Boolean>
        get() = _isFrontCamera

    private val _isStarted = MutableLiveData(cameraUseCase.isStarted)
    val isStarted: LiveData<Boolean>
        get() = _isStarted

    init {
        cameraUseCase.setListener(object: CameraUseCase.Listener{
            override fun onStart() {
                _isStarted.postValue(true)
            }

            override fun onStop() {
                _isStarted.postValue(false)
            }

        })

        _isFrontCamera.postValue(cameraUseCase.isFrontCamera)
        _isStarted.postValue(cameraUseCase.isStarted)
    }

    override fun onCleared() {
        super.onCleared()
        cameraUseCase.onCleared()
    }

    fun start() {
        cameraUseCase.start()
    }

    fun stop() {
        cameraUseCase.stop()
    }

    fun setCameraType(isFront: Boolean) {
        _isFrontCamera.postValue(isFront)
        cameraUseCase.setCameraType(isFront)
    }

    fun addVideoView(videoView: PlanetKitVideoView) {
        cameraUseCase.addVideoView(videoView)
    }

    fun removeVideoView(videoView: PlanetKitVideoView) {
        cameraUseCase.removeCameraVideoView(videoView)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as UiKitApplication).appContainer.kitRepository
                    ?: throw Exception("")
                CameraViewModel(CameraUseCase(repository))
            }
        }
    }
}