package com.thehand.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thehand.android.data.dao.AddendumDao
import com.thehand.android.data.dao.EntryDao
import com.thehand.android.data.dao.ThreadDao
import com.thehand.android.data.dao.TrustedHandDao
import com.thehand.android.data.model.Addendum
import com.thehand.android.data.model.Entry
import com.thehand.android.data.model.SharedEntry
import com.thehand.android.data.model.Thread
import com.thehand.android.data.model.TrustedHand

@Database(
    entities = [
        Entry::class,
        Addendum::class,
        Thread::class,
        TrustedHand::class,
        SharedEntry::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TheHandDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun addendumDao(): AddendumDao
    abstract fun threadDao(): ThreadDao
    abstract fun trustedHandDao(): TrustedHandDao
}
