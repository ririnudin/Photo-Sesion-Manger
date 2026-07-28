package com.example.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.AppSettings
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    val settingsState: StateFlow<AppSettings> = settingsRepository.settingsState

    fun updateSettings(newSettings: AppSettings) {
        settingsRepository.updateSettings(newSettings)
    }

    fun toggleAutoUpload(enabled: Boolean) {
        updateSettings(settingsState.value.copy(autoUploadEnabled = enabled))
    }

    fun toggleWifiOnly(wifiOnly: Boolean) {
        updateSettings(settingsState.value.copy(wifiOnly = wifiOnly))
    }

    fun toggleWatermark(enabled: Boolean) {
        updateSettings(settingsState.value.copy(watermarkEnabled = enabled))
    }

    fun setWatermarkText(text: String) {
        updateSettings(settingsState.value.copy(watermarkText = text))
    }

    fun setUploadQuality(quality: String) {
        updateSettings(settingsState.value.copy(uploadQuality = quality))
    }

    fun setAppTheme(theme: String) {
        updateSettings(settingsState.value.copy(appTheme = theme))
    }

    fun setMainFolderName(folderName: String) {
        updateSettings(settingsState.value.copy(mainFolderName = folderName))
    }
}
