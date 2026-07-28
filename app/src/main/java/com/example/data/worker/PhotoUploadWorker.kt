package com.example.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.repository.DriveRepository
import com.example.data.repository.SessionRepository
import com.example.data.repository.SettingsRepository

class PhotoUploadWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(appContext)
        val driveRepository = DriveRepository(appContext)
        val settingsRepository = SettingsRepository(appContext)
        val sessionRepository = SessionRepository(appContext, database, driveRepository, settingsRepository)

        val activeSession = database.sessionDao().getActiveSessionSync() ?: return Result.success()

        return try {
            sessionRepository.triggerUploadQueue(activeSession.id)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "photo_upload_worker"

        fun enqueueUpload(context: Context, wifiOnly: Boolean) {
            val networkType = if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(networkType)
                .build()

            val uploadWorkRequest = OneTimeWorkRequestBuilder<PhotoUploadWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                uploadWorkRequest
            )
        }
    }
}
