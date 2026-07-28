package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState: StateFlow<AppSettings> = _settingsState.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            googleAccountName = prefs.getString("google_account_name", "Fotografer Event Pro") ?: "Fotografer Event Pro",
            googleAccountEmail = prefs.getString("google_account_email", "photographer@gmail.com") ?: "photographer@gmail.com",
            isSignedIn = prefs.getBoolean("is_signed_in", true),
            mainFolderName = prefs.getString("main_folder_name", "Photo Session Manager") ?: "Photo Session Manager",
            autoUploadEnabled = prefs.getBoolean("auto_upload_enabled", true),
            wifiOnly = prefs.getBoolean("wifi_only", false),
            watermarkEnabled = prefs.getBoolean("watermark_enabled", false),
            watermarkText = prefs.getString("watermark_text", "© Photo Session Manager") ?: "© Photo Session Manager",
            uploadQuality = prefs.getString("upload_quality", "HIGH") ?: "HIGH",
            appTheme = prefs.getString("app_theme", "LIGHT") ?: "LIGHT"
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit().apply {
            putString("google_account_name", newSettings.googleAccountName)
            putString("google_account_email", newSettings.googleAccountEmail)
            putBoolean("is_signed_in", newSettings.isSignedIn)
            putString("main_folder_name", newSettings.mainFolderName)
            putBoolean("auto_upload_enabled", newSettings.autoUploadEnabled)
            putBoolean("wifi_only", newSettings.wifiOnly)
            putBoolean("watermark_enabled", newSettings.watermarkEnabled)
            putString("watermark_text", newSettings.watermarkText)
            putString("upload_quality", newSettings.uploadQuality)
            putString("app_theme", newSettings.appTheme)
            apply()
        }
        _settingsState.value = newSettings
    }

    fun getSettings(): AppSettings = _settingsState.value
}
