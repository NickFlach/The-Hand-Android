package com.thehand.android.data.repository

import com.thehand.android.data.dao.AddendumDao
import com.thehand.android.data.dao.EntryDao
import com.thehand.android.data.model.Addendum
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao,
    private val addendumDao: AddendumDao
) {
    fun getAllEntries(): Flow<List<Entry>> = entryDao.getAllEntries()

    fun getEntryById(id: Long): Flow<Entry?> = entryDao.getEntryById(id)

    fun getEntriesByType(type: EntryType): Flow<List<Entry>> =
        entryDao.getEntriesByType(type)

    fun getEntriesByThread(threadId: Long): Flow<List<Entry>> =
        entryDao.getEntriesByThread(threadId)

    fun getEntriesByMonth(yearMonth: String): Flow<List<Entry>> =
        entryDao.getEntriesByMonth(yearMonth)

    fun getAddendumsForEntry(entryId: Long): Flow<List<Addendum>> =
        addendumDao.getAddendumsForEntry(entryId)

    suspend fun createEntry(entry: Entry): Long {
        return entryDao.insertEntry(entry.copy(
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            isLocked = false
        ))
    }

    suspend fun updateEntry(entry: Entry) {
        if (entry.canEdit()) {
            entryDao.updateEntry(entry.copy(updatedAt = Instant.now()))
        }
    }

    suspend fun deleteEntry(entry: Entry) {
        entryDao.deleteEntry(entry)
    }

    suspend fun addAddendum(entryId: Long, content: String): Long {
        return addendumDao.insertAddendum(
            Addendum(
                entryId = entryId,
                content = content,
                createdAt = Instant.now()
            )
        )
    }

    suspend fun getPatternData(startTime: Instant, endTime: Instant): Map<EntryType, Int> {
        return entryDao.getTypeDistribution(startTime, endTime)
    }
}
