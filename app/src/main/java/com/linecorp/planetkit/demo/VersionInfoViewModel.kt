package com.linecorp.planetkit.demo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.linecorp.planetkit.demo.uikit.UiKitApplication
import com.linecorp.planetkit.demo.uikit.consts.StringSet
import com.linecorp.planetkit.demo.uikit.usecases.GetSDKVersionUseCase

class VersionInfoViewModel(
    private val sdkVersionUseCase: GetSDKVersionUseCase
): ViewModel() {
    fun getAppVersion(context: Context): String {
        return "${context.getText(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_main_versioninfo1)}".
            replace("{{major.minor.patch}}", BuildConfig.VERSION_NAME)
    }

    fun getSDKVersion(context: Context): String {
        val patchVersion = getPatchVersion(sdkVersionUseCase.sdkVersion)
        return "${context.getText(com.linecorp.planetkit.demo.uikit.R.string.lp_demoapp_main_versioninfo2)}".
            replace("{{major.minor.patch}}", patchVersion)
    }

    private fun getPatchVersion(version: String): String {
        val versionParts = version.split(".")
        return if (versionParts.size >= 3) {
            versionParts.take(3).joinToString(".")
        } else {
            version
        }
    }

    companion object {
        private const val TAG = "${StringSet.LOG_UIKIT}PlanetKitVersionInfoViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as UiKitApplication).appContainer.kitRepository
                VersionInfoViewModel(GetSDKVersionUseCase(repository))
            }
        }
    }
}