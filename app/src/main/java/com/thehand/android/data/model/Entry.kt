package com.thehand.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: EntryType,
    val whoWhat: String,
    val whatCost: String,
    val whatDifferently: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isLocked: Boolean = false,
    val threadId: Long? = null
)

enum class EntryType {
    BUILT,
    HELPED,
    LEARNED
}

fun Entry.canEdit(): Boolean {
    val now = Instant.now()
    val twentyFourHoursLater = createdAt.plusSeconds(24 * 60 * 60)
    return !isLocked && now.isBefore(twentyFourHoursLater)
}

fun Entry.lockIfExpired(): Entry {
    return if (!isLocked && !canEdit()) {
        copy(isLocked = true)
    } else {
        this
    }
}
