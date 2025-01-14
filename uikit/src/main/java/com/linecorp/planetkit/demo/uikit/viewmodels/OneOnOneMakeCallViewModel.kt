package com.linecorp.planetkit.demo.uikit.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.repositories.models.ApiError
import com.linecorp.planetkit.demo.uikit.usecases.AccessTokenUseCase
import com.linecorp.planetkit.demo.uikit.usecases.OneOnOneMakeCallUseCase
import org.json.JSONException

class OneOnOneMakeCallViewModel(
    private val oneOnOneMakeCallUseCase: OneOnOneMakeCallUseCase,
    private val accessTokenUseCase: AccessTokenUseCase,
): ViewModel() {

    data class MakeCallResult(
        val isSuccessful: Boolean,
        val isVideoCall: Boolean,
        val planetKitStartFailReason: String?,
        val exceptionMsg: String?,
        val callInstanceId: Int = -1,
        val isUnhandledException: Boolean = false
    )

    private val _makeCallResult = MutableLiveData<MakeCallResult?>(null)
    val makeCallResult: LiveData<MakeCallResult?>
        get() = _makeCallResult


    override fun onCleared() {
        super.onCleared()
    }

    suspend fun makeCall(peerId: String, isVideoCall: Boolean) {
        try {
            accessTokenUseCase.getAccessToken()?.let { accessToken ->
                val result = oneOnOneMakeCallUseCase.makeCall(peerId, isVideoCall, accessToken)
                if (result.reason == PlanetKitStartFailReason.NONE) {
                    _makeCallResult.postValue(MakeCallResult(
                        true,
                        isVideoCall,
                        result.reason.toString(),
                        null,
                        result.call!!.instanceId,
                    ))
                }
                else {
                    _makeCallResult.postValue(MakeCallResult(
                        false,
                        isVideoCall,
                        result.reason.toString(),
                        null
                    ))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            _makeCallResult.postValue(MakeCallResult(
                false,
                isVideoCall,
                null,
                "${e.message}"
            ))
        } catch (e: ApiError) {
            e.printStackTrace()
            _makeCallResult.postValue(MakeCallResult(
                false,
                isVideoCall,
                null,
                "${e.message}"
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            _makeCallResult.postValue(
                MakeCallResult(
                false,
                isVideoCall,
                null,
                "${e.message}",
                isUnhandledException = true
            )
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as UiKitApplication).appContainer
                val serviceLocatorRepository = appContainer.serviceLocatorRepository
                val callRepositoryContainer = appContainer.callRepositoryContainer
                val appServerRepository = appContainer.appServerRepository

                OneOnOneMakeCallViewModel(
                    OneOnOneMakeCallUseCase(serviceLocatorRepository, callRepositoryContainer),
                    AccessTokenUseCase(appServerRepository, serviceLocatorRepository)
                )
            }
        }
    }
}