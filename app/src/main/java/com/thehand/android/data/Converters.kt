package com.thehand.android.data

import androidx.room.TypeConverter
import com.thehand.android.data.model.EntryType
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochSecond(it) }
    }

    @TypeConverter
    fun toTimestamp(instant: Instant?): Long? {
        return instant?.epochSecond
    }

    @TypeConverter
    fun fromEntryType(value: EntryType): String {
        return value.name
    }

    @TypeConverter
    fun toEntryType(value: String): EntryType {
        return EntryType.valueOf(value)
    }
}
