package com.example.ui.screens.activesession

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppSettings
import com.example.data.model.PhotoItem
import com.example.data.model.PhotoSession
import com.example.data.repository.DriveRepository
import com.example.data.repository.SessionRepository
import com.example.data.repository.SettingsRepository
import com.example.data.worker.PhotoUploadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ActiveSessionViewModel(
    application: Application,
    val sessionId: Long
) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val driveRepository = DriveRepository(application)
    private val settingsRepository = SettingsRepository(application)
    val sessionRepository = SessionRepository(application, database, driveRepository, settingsRepository)

    val sessionState: StateFlow<PhotoSession?> = sessionRepository.database.sessionDao()
        .getSessionById(sessionId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val photosList: StateFlow<List<PhotoItem>> = sessionRepository.getPhotosForSession(sessionId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val settings: StateFlow<AppSettings> = settingsRepository.settingsState

    private val _isEndingSession = MutableStateFlow(false)
    val isEndingSession: StateFlow<Boolean> = _isEndingSession.asStateFlow()

    /**
     * Called when a photo is captured or imported.
     */
    fun addCapturedPhoto(localPath: String, fileName: String, fileSize: Long) {
        viewModelScope.launch {
            sessionRepository.addPhotoToSession(
                sessionId = sessionId,
                localPath = localPath,
                fileName = fileName,
                fileSize = fileSize
            )
            // Schedule WorkManager upload
            PhotoUploadWorker.enqueueUpload(getApplication(), settings.value.wifiOnly)
        }
    }

    /**
     * Import photo from gallery URI.
     */
    fun addGalleryPhotoUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val photosDir = File(context.filesDir, "session_photos_$sessionId")
                if (!photosDir.exists()) photosDir.mkdirs()

                val fileName = "gallery_${System.currentTimeMillis()}.jpg"
                val photoFile = File(photosDir, fileName)

                FileOutputStream(photoFile).use { output ->
                    inputStream.copyTo(output)
                }

                addCapturedPhoto(
                    localPath = photoFile.absolutePath,
                    fileName = fileName,
                    fileSize = photoFile.length()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Manual trigger to re-upload failed or queued items.
     */
    fun retryUploads() {
        viewModelScope.launch {
            sessionRepository.triggerUploadQueue(sessionId)
        }
    }

    /**
     * End session button pressed.
     */
    fun endSession(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isEndingSession.value = true
            sessionRepository.endSession(sessionId)
            _isEndingSession.value = false
            onComplete()
        }
    }

    class Factory(
        private val application: Application,
        private val sessionId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActiveSessionViewModel::class.java)) {
                return ActiveSessionViewModel(application, sessionId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
