package com.thehand.android.data.dao

import androidx.room.*
import com.thehand.android.data.model.Thread
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreadDao {
    @Query("SELECT * FROM threads WHERE isClosed = 0 ORDER BY createdAt DESC")
    fun getActiveThreads(): Flow<List<Thread>>

    @Query("SELECT * FROM threads WHERE isClosed = 1 ORDER BY closedAt DESC")
    fun getClosedThreads(): Flow<List<Thread>>

    @Query("SELECT * FROM threads WHERE id = :threadId")
    fun getThreadById(threadId: Long): Flow<Thread?>

    @Query("SELECT * FROM threads ORDER BY createdAt DESC")
    fun getAllThreads(): Flow<List<Thread>>

    @Insert
    suspend fun insertThread(thread: Thread): Long

    @Update
    suspend fun updateThread(thread: Thread)

    @Delete
    suspend fun deleteThread(thread: Thread)
}
