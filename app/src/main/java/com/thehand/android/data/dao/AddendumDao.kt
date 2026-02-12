package com.thehand.android.data.dao

import androidx.room.*
import com.thehand.android.data.model.Addendum
import kotlinx.coroutines.flow.Flow

@Dao
interface AddendumDao {
    @Query("SELECT * FROM addendums WHERE entryId = :entryId ORDER BY createdAt ASC")
    fun getAddendumsForEntry(entryId: Long): Flow<List<Addendum>>

    @Insert
    suspend fun insertAddendum(addendum: Addendum): Long

    @Delete
    suspend fun deleteAddendum(addendum: Addendum)
}
