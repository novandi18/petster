package com.novandiramadhan.petster.di

import com.novandiramadhan.petster.data.repository.AssistantRepositoryImpl
import com.novandiramadhan.petster.data.repository.AuthRepositoryImpl
import com.novandiramadhan.petster.data.repository.FavoritePetRepositoryImpl
import com.novandiramadhan.petster.data.repository.PetImageRepositoryImpl
import com.novandiramadhan.petster.data.repository.PetRepositoryImpl
import com.novandiramadhan.petster.domain.repository.AssistantRepository
import com.novandiramadhan.petster.domain.repository.AuthRepository
import com.novandiramadhan.petster.domain.repository.FavoritePetRepository
import com.novandiramadhan.petster.domain.repository.PetImageRepository
import com.novandiramadhan.petster.domain.repository.PetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindPetImageRepository(petImageRepositoryImpl: PetImageRepositoryImpl): PetImageRepository

    @Binds
    abstract fun bindPetRepository(petRepositoryImpl: PetRepositoryImpl): PetRepository

    @Binds
    abstract fun bindFavoritePetRepository(
        favoritePetRepositoryImpl: FavoritePetRepositoryImpl
    ): FavoritePetRepository

    @Binds
    abstract fun bindAssistantRepository(
        favoritePetRepositoryImpl: AssistantRepositoryImpl
    ): AssistantRepository
}