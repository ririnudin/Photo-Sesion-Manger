package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_sessions")
data class PhotoSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val qrCodeData: String? = null,
    val driveFolderId: String,
    val driveFolderUrl: String,
    val folderName: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isActive: Boolean = true,
    val totalPhotos: Int = 0,
    val uploadedPhotos: Int = 0,
    val pendingPhotos: Int = 0
)
