package com.thehand.android.data.repository

import com.thehand.android.data.dao.ThreadDao
import com.thehand.android.data.model.Thread
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadRepository @Inject constructor(
    private val threadDao: ThreadDao
) {
    fun getActiveThreads(): Flow<List<Thread>> = threadDao.getActiveThreads()

    fun getClosedThreads(): Flow<List<Thread>> = threadDao.getClosedThreads()

    fun getAllThreads(): Flow<List<Thread>> = threadDao.getAllThreads()

    fun getThreadById(id: Long): Flow<Thread?> = threadDao.getThreadById(id)

    suspend fun createThread(name: String, description: String): Long {
        return threadDao.insertThread(
            Thread(
                name = name,
                description = description,
                createdAt = Instant.now(),
                isClosed = false
            )
        )
    }

    suspend fun closeThread(thread: Thread) {
        threadDao.updateThread(
            thread.copy(
                isClosed = true,
                closedAt = Instant.now()
            )
        )
    }

    suspend fun reopenThread(thread: Thread) {
        threadDao.updateThread(
            thread.copy(
                isClosed = false,
                closedAt = null
            )
        )
    }

    suspend fun deleteThread(thread: Thread) {
        threadDao.deleteThread(thread)
    }
}
