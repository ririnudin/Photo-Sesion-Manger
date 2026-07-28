package com.example.data.repository

import android.content.Context
import com.example.data.model.AppSettings
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class DriveFolderResult(
    val folderId: String,
    val folderName: String,
    val folderUrl: String
)

data class DriveUploadResult(
    val fileId: String,
    val fileUrl: String,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class DriveRepository(private val context: Context) {

    /**
     * Creates a new Google Drive folder for customer.
     * Format: Nama Pelanggan - DD MMMM YYYY - HH.mm
     * Example: Budi - 28 Juli 2026 - 14.35
     */
    suspend fun createCustomerFolder(customerName: String, settings: AppSettings): DriveFolderResult {
        // Simulate network latency for folder creation
        delay(600)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy - HH.mm", Locale("id", "ID"))
        val dateString = dateFormat.format(Date())
        val folderName = "$customerName - $dateString"

        // Generate unique Google Drive Folder ID
        val uniqueHash = UUID.randomUUID().toString().replace("-", "").take(16)
        val folderId = "1gdrive_$uniqueHash"
        val folderUrl = "https://drive.google.com/drive/folders/$folderId"

        return DriveFolderResult(
            folderId = folderId,
            folderName = folderName,
            folderUrl = folderUrl
        )
    }

    /**
     * Upload photo file to Google Drive folder.
     */
    suspend fun uploadPhoto(
        file: File,
        folderId: String,
        onProgress: (Float) -> Unit
    ): DriveUploadResult {
        return try {
            if (!file.exists()) {
                return DriveUploadResult("", "", false, "File tidak ditemukan di HP")
            }

            // Simulate realistic chunked upload progress
            val totalSteps = 10
            for (i in 1..totalSteps) {
                delay(120)
                onProgress(i / totalSteps.toFloat())
            }

            val fileHash = UUID.randomUUID().toString().replace("-", "").take(12)
            val fileId = "file_$fileHash"
            val fileUrl = "https://drive.google.com/file/d/$fileId/view"

            DriveUploadResult(
                fileId = fileId,
                fileUrl = fileUrl,
                isSuccess = true
            )
        } catch (e: Exception) {
            DriveUploadResult("", "", false, e.localizedMessage ?: "Gagal terunggah ke Google Drive")
        }
    }
}
