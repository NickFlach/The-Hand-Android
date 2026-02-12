package com.thehand.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "trusted_hands")
data class TrustedHand(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val identifier: String, // Email or unique ID
    val addedAt: Instant
)

@Entity(tableName = "shared_entries")
data class SharedEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: Long,
    val trustedHandId: Long,
    val reason: String,
    val sharedAt: Instant
)
