package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PhotoItem
import com.example.data.model.UploadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo_items WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getPhotosForSession(sessionId: Long): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photo_items WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    suspend fun getPhotosForSessionSync(sessionId: Long): List<PhotoItem>

    @Query("SELECT * FROM photo_items WHERE uploadStatus = 'QUEUED' OR uploadStatus = 'FAILED'")
    suspend fun getPendingPhotosForUpload(): List<PhotoItem>

    @Query("SELECT COUNT(*) FROM photo_items WHERE sessionId = :sessionId")
    fun getTotalPhotoCount(sessionId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM photo_items WHERE sessionId = :sessionId AND uploadStatus = 'UPLOADED'")
    fun getUploadedPhotoCount(sessionId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM photo_items WHERE sessionId = :sessionId AND (uploadStatus = 'QUEUED' OR uploadStatus = 'UPLOADING')")
    fun getPendingPhotoCount(sessionId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItem): Long

    @Update
    suspend fun updatePhoto(photo: PhotoItem)

    @Query("UPDATE photo_items SET uploadStatus = :status, driveFileId = :driveFileId, errorMessage = :errorMessage WHERE id = :photoId")
    suspend fun updatePhotoStatus(
        photoId: Long,
        status: UploadStatus,
        driveFileId: String? = null,
        errorMessage: String? = null
    )

    @Query("DELETE FROM photo_items WHERE id = :photoId")
    suspend fun deletePhoto(photoId: Long)
}
