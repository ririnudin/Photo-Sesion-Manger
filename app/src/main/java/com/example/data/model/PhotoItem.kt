package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UploadStatus {
    QUEUED,
    UPLOADING,
    UPLOADED,
    FAILED
}

@Entity(
    tableName = "photo_items",
    foreignKeys = [
        ForeignKey(
            entity = PhotoSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class PhotoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val localPath: String,
    val fileName: String,
    val fileSize: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val uploadStatus: UploadStatus = UploadStatus.QUEUED,
    val driveFileId: String? = null,
    val errorMessage: String? = null
)
