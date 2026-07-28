package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.PhotoItem
import com.example.data.model.PhotoSession
import com.example.data.model.UploadStatus
import com.example.util.WatermarkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SessionRepository(
    private val context: Context,
    val database: AppDatabase,
    private val driveRepository: DriveRepository,
    private val settingsRepository: SettingsRepository
) {
    val sessionDao = database.sessionDao()
    val photoDao = database.photoDao()

    val allSessions: Flow<List<PhotoSession>> = sessionDao.getAllSessions()
    val activeSession: Flow<PhotoSession?> = sessionDao.getActiveSession()

    fun getPhotosForSession(sessionId: Long): Flow<List<PhotoItem>> {
        return photoDao.getPhotosForSession(sessionId)
    }

    fun searchSessions(query: String): Flow<List<PhotoSession>> {
        return sessionDao.searchSessions(query)
    }

    /**
     * Start a new photo session for a customer.
     */
    suspend fun createNewSession(customerName: String, qrCodeData: String?): Long = withContext(Dispatchers.IO) {
        // First end any existing active session
        val currentActive = sessionDao.getActiveSessionSync()
        if (currentActive != null) {
            sessionDao.endSession(currentActive.id)
        }

        // Create Drive Folder
        val settings = settingsRepository.getSettings()
        val driveFolder = driveRepository.createCustomerFolder(customerName, settings)

        val newSession = PhotoSession(
            customerName = customerName,
            qrCodeData = qrCodeData,
            driveFolderId = driveFolder.folderId,
            driveFolderUrl = driveFolder.folderUrl,
            folderName = driveFolder.folderName,
            startTime = System.currentTimeMillis(),
            isActive = true
        )

        val sessionId = sessionDao.insertSession(newSession)
        sessionId
    }

    /**
     * Add captured photo to session queue and trigger auto-upload.
     */
    suspend fun addPhotoToSession(
        sessionId: Long,
        localPath: String,
        fileName: String,
        fileSize: Long
    ): Long = withContext(Dispatchers.IO) {
        val photoItem = PhotoItem(
            sessionId = sessionId,
            localPath = localPath,
            fileName = fileName,
            fileSize = fileSize,
            timestamp = System.currentTimeMillis(),
            uploadStatus = UploadStatus.QUEUED
        )

        val photoId = photoDao.insertPhoto(photoItem)
        updateSessionCounts(sessionId)

        // Trigger background upload if auto-upload is enabled
        val settings = settingsRepository.getSettings()
        if (settings.autoUploadEnabled) {
            triggerUploadQueue(sessionId)
        }

        photoId
    }

    /**
     * Process photo upload queue for the active session.
     */
    suspend fun triggerUploadQueue(sessionId: Long) = withContext(Dispatchers.IO) {
        val session = sessionDao.getSessionByIdSync(sessionId) ?: return@withContext
        val pendingPhotos = photoDao.getPendingPhotosForUpload().filter { it.sessionId == sessionId }
        if (pendingPhotos.isEmpty()) return@withContext

        val settings = settingsRepository.getSettings()

        for (photo in pendingPhotos) {
            photoDao.updatePhotoStatus(photo.id, UploadStatus.UPLOADING)

            // Apply watermark and quality compression if configured
            val processedFile = WatermarkUtil.processPhoto(
                context = context,
                inputPath = photo.localPath,
                watermarkEnabled = settings.watermarkEnabled,
                watermarkText = settings.watermarkText,
                uploadQuality = settings.uploadQuality
            )

            // Perform upload to Google Drive
            val uploadResult = driveRepository.uploadPhoto(
                file = processedFile,
                folderId = session.driveFolderId,
                onProgress = { /* progress callback handled in status */ }
            )

            if (uploadResult.isSuccess) {
                photoDao.updatePhotoStatus(
                    photoId = photo.id,
                    status = UploadStatus.UPLOADED,
                    driveFileId = uploadResult.fileId
                )
            } else {
                photoDao.updatePhotoStatus(
                    photoId = photo.id,
                    status = UploadStatus.FAILED,
                    errorMessage = uploadResult.errorMessage
                )
            }

            updateSessionCounts(sessionId)
        }
    }

    /**
     * End current active session.
     */
    suspend fun endSession(sessionId: Long) = withContext(Dispatchers.IO) {
        sessionDao.endSession(sessionId, System.currentTimeMillis())
    }

    /**
     * Delete session.
     */
    suspend fun deleteSession(sessionId: Long) = withContext(Dispatchers.IO) {
        sessionDao.deleteSession(sessionId)
    }

    private suspend fun updateSessionCounts(sessionId: Long) {
        val session = sessionDao.getSessionByIdSync(sessionId) ?: return
        val allPhotos = photoDao.getPhotosForSessionSync(sessionId)

        val total = allPhotos.size
        val uploaded = allPhotos.count { it.uploadStatus == UploadStatus.UPLOADED }
        val pending = allPhotos.count { it.uploadStatus == UploadStatus.QUEUED || it.uploadStatus == UploadStatus.UPLOADING || it.uploadStatus == UploadStatus.FAILED }

        sessionDao.updateSession(
            session.copy(
                totalPhotos = total,
                uploadedPhotos = uploaded,
                pendingPhotos = pending
            )
        )
    }
}
