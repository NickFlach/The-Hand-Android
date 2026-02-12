package com.thehand.android.di

import android.content.Context
import androidx.room.Room
import com.thehand.android.data.TheHandDatabase
import com.thehand.android.data.dao.AddendumDao
import com.thehand.android.data.dao.EntryDao
import com.thehand.android.data.dao.ThreadDao
import com.thehand.android.data.dao.TrustedHandDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TheHandDatabase {
        return Room.databaseBuilder(
            context,
            TheHandDatabase::class.java,
            "the_hand_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideEntryDao(database: TheHandDatabase): EntryDao {
        return database.entryDao()
    }

    @Provides
    fun provideAddendumDao(database: TheHandDatabase): AddendumDao {
        return database.addendumDao()
    }

    @Provides
    fun provideThreadDao(database: TheHandDatabase): ThreadDao {
        return database.threadDao()
    }

    @Provides
    fun provideTrustedHandDao(database: TheHandDatabase): TrustedHandDao {
        return database.trustedHandDao()
    }
}
