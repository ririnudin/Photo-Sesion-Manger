package com.example.ui.screens.sessionlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.PhotoSession
import com.example.data.repository.DriveRepository
import com.example.data.repository.SessionRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val driveRepository = DriveRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val sessionRepository = SessionRepository(application, database, driveRepository, settingsRepository)

    var searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val sessionList: StateFlow<List<PhotoSession>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                sessionRepository.allSessions
            } else {
                sessionRepository.searchSessions(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            sessionRepository.deleteSession(sessionId)
        }
    }
}
