package com.novandiramadhan.petster.di

import com.novandiramadhan.petster.domain.interactor.AssistantInteractor
import com.novandiramadhan.petster.domain.interactor.AuthInteractor
import com.novandiramadhan.petster.domain.interactor.CommunityInteractor
import com.novandiramadhan.petster.domain.interactor.FavoritePetInteractor
import com.novandiramadhan.petster.domain.interactor.PetImageInteractor
import com.novandiramadhan.petster.domain.interactor.PetInteractor
import com.novandiramadhan.petster.domain.usecase.AssistantUseCase
import com.novandiramadhan.petster.domain.usecase.AuthUseCase
import com.novandiramadhan.petster.domain.usecase.CommunityUseCase
import com.novandiramadhan.petster.domain.usecase.FavoritePetUseCase
import com.novandiramadhan.petster.domain.usecase.PetImageUseCase
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    @Singleton
    abstract fun bindAuthUseCase(interactor: AuthInteractor): AuthUseCase

    @Binds
    @Singleton
    abstract fun bindPetImageUseCase(interactor: PetImageInteractor): PetImageUseCase

    @Binds
    @Singleton
    abstract fun bindPetUseCase(interactor: PetInteractor): PetUseCase

    @Binds
    @Singleton
    abstract fun bindFavoritePetUseCase(interactor: FavoritePetInteractor): FavoritePetUseCase

    @Binds
    @Singleton
    abstract fun bindAssistantUseCase(interactor: AssistantInteractor): AssistantUseCase

    @Binds
    @Singleton
    abstract fun bindCommunityUseCase(interactor: CommunityInteractor): CommunityUseCase
}