package com.example.ui.screens.newsession

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.DriveRepository
import com.example.data.repository.SessionRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NewSessionUiState {
    object Idle : NewSessionUiState()
    object CreatingFolder : NewSessionUiState()
    data class Success(val sessionId: Long) : NewSessionUiState()
    data class Error(val message: String) : NewSessionUiState()
}

class NewSessionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val driveRepository = DriveRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val sessionRepository = SessionRepository(application, database, driveRepository, settingsRepository)

    var customerName = MutableStateFlow("")
    var qrCodeData = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow<NewSessionUiState>(NewSessionUiState.Idle)
    val uiState: StateFlow<NewSessionUiState> = _uiState.asStateFlow()

    fun updateCustomerName(name: String) {
        customerName.value = name
    }

    fun setQrCode(qrData: String) {
        qrCodeData.value = qrData
        if (customerName.value.isBlank()) {
            // Extract clean name if QR code contains prefix
            val cleanName = qrData.replace("PELANGGAN-", "").replace("CUST-", "").trim()
            customerName.value = cleanName
        }
    }

    fun createSession() {
        val name = customerName.value.trim()
        if (name.isBlank()) {
            _uiState.value = NewSessionUiState.Error("Nama pelanggan wajib diisi!")
            return
        }

        viewModelScope.launch {
            _uiState.value = NewSessionUiState.CreatingFolder
            try {
                val sessionId = sessionRepository.createNewSession(
                    customerName = name,
                    qrCodeData = qrCodeData.value
                )
                _uiState.value = NewSessionUiState.Success(sessionId)
            } catch (e: Exception) {
                _uiState.value = NewSessionUiState.Error(e.localizedMessage ?: "Gagal membuat folder Google Drive")
            }
        }
    }

    fun resetState() {
        _uiState.value = NewSessionUiState.Idle
    }
}
