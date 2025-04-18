package com.novandiramadhan.petster.di

import android.content.Context
import androidx.room.Room
import com.novandiramadhan.petster.common.RoomConstants
import com.novandiramadhan.petster.data.local.room.PetsterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PetsterDatabase {
        return Room.databaseBuilder(
            context,
            PetsterDatabase::class.java,
            RoomConstants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: PetsterDatabase) = database.assistantDao()
}