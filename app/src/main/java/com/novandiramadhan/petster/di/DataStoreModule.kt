package com.novandiramadhan.petster.di

import android.content.Context
import com.novandiramadhan.petster.data.local.datastore.AuthDataStoreImpl
import com.novandiramadhan.petster.data.local.datastore.WelcomeDataStoreImpl
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.datastore.WelcomeDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideWelcomeDataStore(@ApplicationContext context: Context): WelcomeDataStore =
        WelcomeDataStoreImpl(context)

    @Provides
    @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): AuthDataStore =
        AuthDataStoreImpl(context)
}