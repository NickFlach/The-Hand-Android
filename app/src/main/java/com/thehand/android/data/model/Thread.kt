package com.thehand.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "threads")
data class Thread(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdAt: Instant,
    val closedAt: Instant? = null,
    val isClosed: Boolean = false
)
