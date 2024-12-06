package com.linecorp.planetkit.demo.uikit.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.PlanetKitStartFailReason
import com.linecorp.planetkit.demo.uikit.NotificationService
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.repositories.models.ApiError
import com.linecorp.planetkit.demo.uikit.uikitdata.UIKitGroupCallParam
import com.linecorp.planetkit.demo.uikit.usecases.AccessTokenUseCase
import com.linecorp.planetkit.demo.uikit.usecases.GroupCallStateUseCase
import org.json.JSONException

class GroupCallStateViewModel(
    private val groupCallStateUserCase: GroupCallStateUseCase,
    private val accessTokenUseCase: AccessTokenUseCase
): ViewModel() {

    private val _isConnected = MutableLiveData<Boolean?>(null)
    val isConnected: LiveData<Boolean?>
        get() = _isConnected

    private var _disconnectReason = ""
    val disconnectReason: String
        get() = _disconnectReason

    init {
        _isConnected.postValue(groupCallStateUserCase.isConnected)
        groupCallStateUserCase.addStateListener(object :GroupCallStateUseCase.Listener {
            override fun onConnected(context: Context) {
                _isConnected.postValue(true)
            }

            override fun onDisconnected(context: Context, reason: String) {
                clearNotification(context)
                _isConnected.postValue(false)
                _disconnectReason = reason
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        groupCallStateUserCase.onCleared()
    }

    data class JoinResult(
        val isSuccessful: Boolean,
        val planetKitStartFailReason: String?,
        val exceptionMsg: String?,
        val isUnhandledException: Boolean = false
    )

    private val _joinResult = MutableLiveData<JoinResult?>(null)
    val joinResult: LiveData<JoinResult?>
        get() = _joinResult
    suspend fun join(param: UIKitGroupCallParam) {
        try {
            accessTokenUseCase.getAccessToken()?.let {
                val reason = groupCallStateUserCase.join(param, it)
                if (reason == PlanetKitStartFailReason.NONE) {
                    _joinResult.postValue(JoinResult(
                        true,
                        reason.toString(),
                        null,
                    ))
                }
                else {
                    _joinResult.postValue(JoinResult(
                        false,
                        reason.toString(),
                        null
                    ))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            _joinResult.postValue(JoinResult(
                false,
                null,
                "${e.message}"
            ))
        } catch (e: ApiError) {
            e.printStackTrace()
            _joinResult.postValue(JoinResult(
                false,
                null,
                "${e.message}"
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            _joinResult.postValue(JoinResult(
                false,
                null,
                "${e.message}",
                isUnhandledException = true
            ))
        }
    }

    fun leave() {
        groupCallStateUserCase.leave()
    }

    private fun clearNotification(context: Context) {
        NotificationService.clear(context)
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as UiKitApplication).appContainer
                val repository = appContainer.groupCallRepository
                GroupCallStateViewModel(GroupCallStateUseCase(repository), AccessTokenUseCase(appContainer.appServerRepository, appContainer.serviceLocatorRepository))
            }
        }
    }
}