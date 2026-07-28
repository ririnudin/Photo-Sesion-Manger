package com.example.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppSettings
import com.example.data.model.PhotoSession
import com.example.data.repository.DriveRepository
import com.example.data.repository.SessionRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val driveRepository = DriveRepository(application)
    val settingsRepository = SettingsRepository(application)
    val sessionRepository = SessionRepository(application, database, driveRepository, settingsRepository)

    val activeSession: StateFlow<PhotoSession?> = sessionRepository.activeSession
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recentSessions: StateFlow<List<PhotoSession>> = sessionRepository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val settings: StateFlow<AppSettings> = settingsRepository.settingsState

    fun endActiveSession(sessionId: Long) {
        viewModelScope.launch {
            sessionRepository.endSession(sessionId)
        }
    }
}
