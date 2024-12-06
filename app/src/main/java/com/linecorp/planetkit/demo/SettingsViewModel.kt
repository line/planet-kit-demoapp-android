package com.linecorp.planetkit.demo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.repositories.models.ApiError
import com.linecorp.planetkit.demo.uikit.usecases.RegisterUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import retrofit2.HttpException

class SettingsViewModel(private val registerUserUseCase: RegisterUserUseCase): ViewModel() {
    enum class ErrorReason {
        NOT_FOUND_USER_NAME_OR_USER_ID,
        CONFLICT_USER_ID,
        INTERNAL,
    }

    val userName = registerUserUseCase.userName
    val userId = registerUserUseCase.userId
    val expDate
        get() = registerUserUseCase.expDate

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    val isRegistered
        get() = registerUserUseCase.isRegistered
    private val _registered = MutableLiveData(isRegistered)

    val registered: LiveData<Boolean>
        get() = _registered

    private val _errorReason = MutableLiveData<ErrorReason>()
    val errorReason: LiveData<ErrorReason>
        get() = _errorReason

    fun resetUserProfile() {
        registerUserUseCase.resetUser()
        _registered.postValue(false)
    }
    fun registerUserProfile(userName: String, userId: String, notificationType: String) {
        if (userName.isEmpty() || userId.isEmpty()) {
            _errorReason.postValue(ErrorReason.NOT_FOUND_USER_NAME_OR_USER_ID)
            return
        }

        viewModelScope.launch {
            var errorLog: String? = null
            try {
                _loading.postValue(true)
                // delay just for beauty
                delay(300)
                register(userName, userId, notificationType)
                _registered.postValue(true)
            } catch (e: ApiError) {
                errorLog = "Registration failed with ApiError exception ${e.message} \n${Log.getStackTraceString(java.lang.Exception())}"
                _errorReason.postValue(ErrorReason.INTERNAL)
            } catch (e: IOException) {
                errorLog = "Check your network connectivity with IOException \n${Log.getStackTraceString(java.lang.Exception())}"
                _errorReason.postValue(ErrorReason.INTERNAL)
            } catch (e: HttpException) {
                errorLog = "Registration failed with HttpException ${e.message} \n${Log.getStackTraceString(java.lang.Exception())}"
                if (e.code() == HTTP_RESPONSE_CODE_CONFLICT) {
                    _errorReason.postValue(ErrorReason.CONFLICT_USER_ID)
                }
                else {
                    _errorReason.postValue(ErrorReason.INTERNAL)
                }
            } catch (e: Exception) {
                errorLog = "Registration failed with exception ${e.message} \n${Log.getStackTraceString(java.lang.Exception())}"
                _errorReason.postValue(ErrorReason.INTERNAL)
            } finally {
                if (errorLog != null) {
                    Log.e(TAG, errorLog)
                }
                _loading.postValue(false)
            }
        }
    }

    private suspend fun register(userName: String, userId: String, notificationType: String) =
        withContext(Dispatchers.IO) {
            registerUserUseCase.registerUser(userName, userId, notificationType)
        }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}SettingsViewModel"
        private const val HTTP_RESPONSE_CODE_CONFLICT = 409

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as UiKitApplication).appContainer
                val serviceLocatorRepository = appContainer.serviceLocatorRepository
                val appServerRepository = appContainer.appServerRepository
                SettingsViewModel(RegisterUserUseCase(serviceLocatorRepository, appServerRepository))
            }
        }
    }
}