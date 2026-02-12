package com.thehand.android.data.repository

import com.thehand.android.data.dao.TrustedHandDao
import com.thehand.android.data.model.SharedEntry
import com.thehand.android.data.model.TrustedHand
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrustedHandRepository @Inject constructor(
    private val trustedHandDao: TrustedHandDao
) {
    fun getTrustedHands(): Flow<List<TrustedHand>> = trustedHandDao.getTrustedHands()

    fun getTrustedHandById(id: Long): Flow<TrustedHand?> = trustedHandDao.getTrustedHandById(id)

    suspend fun getTrustedHandCount(): Int = trustedHandDao.getTrustedHandCount()

    suspend fun addTrustedHand(name: String, identifier: String): Long {
        return trustedHandDao.insertTrustedHand(
            TrustedHand(
                name = name,
                identifier = identifier,
                addedAt = Instant.now()
            )
        )
    }

    suspend fun removeTrustedHand(hand: TrustedHand) {
        trustedHandDao.deleteTrustedHand(hand)
    }

    suspend fun shareEntryWith(entryId: Long, trustedHandId: Long) {
        trustedHandDao.insertSharedEntry(
            SharedEntry(
                entryId = entryId,
                trustedHandId = trustedHandId,
                sharedAt = Instant.now()
            )
        )
    }

    fun getSharedEntriesForEntry(entryId: Long): Flow<List<SharedEntry>> =
        trustedHandDao.getSharedEntriesForEntry(entryId)
}
