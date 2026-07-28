package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PhotoSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM photo_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<PhotoSession>>

    @Query("SELECT * FROM photo_sessions WHERE isActive = 1 LIMIT 1")
    fun getActiveSession(): Flow<PhotoSession?>

    @Query("SELECT * FROM photo_sessions WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSessionSync(): PhotoSession?

    @Query("SELECT * FROM photo_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Long): Flow<PhotoSession?>

    @Query("SELECT * FROM photo_sessions WHERE id = :sessionId")
    suspend fun getSessionByIdSync(sessionId: Long): PhotoSession?

    @Query("SELECT * FROM photo_sessions WHERE customerName LIKE '%' || :query || '%' ORDER BY startTime DESC")
    fun searchSessions(query: String): Flow<List<PhotoSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PhotoSession): Long

    @Update
    suspend fun updateSession(session: PhotoSession)

    @Query("UPDATE photo_sessions SET isActive = 0, endTime = :endTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM photo_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}
