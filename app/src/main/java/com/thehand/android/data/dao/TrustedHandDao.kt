package com.thehand.android.data.dao

import androidx.room.*
import com.thehand.android.data.model.SharedEntry
import com.thehand.android.data.model.TrustedHand
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustedHandDao {
    @Query("SELECT * FROM trusted_hands ORDER BY addedAt DESC LIMIT 3")
    fun getTrustedHands(): Flow<List<TrustedHand>>

    @Query("SELECT * FROM trusted_hands WHERE id = :handId")
    fun getTrustedHandById(handId: Long): Flow<TrustedHand?>

    @Insert
    suspend fun insertTrustedHand(hand: TrustedHand): Long

    @Update
    suspend fun updateTrustedHand(hand: TrustedHand)

    @Delete
    suspend fun deleteTrustedHand(hand: TrustedHand)

    @Query("SELECT COUNT(*) FROM trusted_hands")
    suspend fun getTrustedHandCount(): Int

    // Shared entries
    @Insert
    suspend fun insertSharedEntry(sharedEntry: SharedEntry): Long

    @Query("SELECT * FROM shared_entries WHERE entryId = :entryId")
    fun getSharedEntriesForEntry(entryId: Long): Flow<List<SharedEntry>>
}
