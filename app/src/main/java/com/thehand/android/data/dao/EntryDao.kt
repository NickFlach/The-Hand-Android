package com.thehand.android.data.dao

import androidx.room.*
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.EntryType
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE id = :entryId")
    fun getEntryById(entryId: Long): Flow<Entry?>

    @Query("SELECT * FROM entries WHERE type = :type ORDER BY createdAt DESC")
    fun getEntriesByType(type: EntryType): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE threadId = :threadId ORDER BY createdAt DESC")
    fun getEntriesByThread(threadId: Long): Flow<List<Entry>>

    @Query("""
        SELECT * FROM entries
        WHERE strftime('%Y-%m', datetime(createdAt, 'unixepoch')) = :yearMonth
        ORDER BY createdAt DESC
    """)
    fun getEntriesByMonth(yearMonth: String): Flow<List<Entry>>

    @Query("""
        SELECT * FROM entries
        WHERE createdAt >= :startTime AND createdAt < :endTime
        ORDER BY createdAt DESC
    """)
    fun getEntriesInRange(startTime: Instant, endTime: Instant): Flow<List<Entry>>

    @Insert
    suspend fun insertEntry(entry: Entry): Long

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("SELECT COUNT(*) FROM entries WHERE type = :type")
    suspend fun getCountByType(type: EntryType): Int

    @Query("""
        SELECT type, COUNT(*) as count
        FROM entries
        WHERE createdAt >= :startTime AND createdAt < :endTime
        GROUP BY type
    """)
    suspend fun getTypeDistribution(startTime: Instant, endTime: Instant): Map<EntryType, Int>
}
