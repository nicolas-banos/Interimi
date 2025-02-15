package com.interimi.interimi.di

import com.interimi.interimi.data.OpenAIRepository
import com.interimi.interimi.data.PreferencesRepository
import com.interimi.interimi.data.UserRepository
import com.interimi.interimi.domain.usecases.UseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUseCases(
        userRepository: UserRepository,
        preferencesRepository: PreferencesRepository,
        openAIRepository: OpenAIRepository
    ): UseCases {
        return UseCases(userRepository, preferencesRepository, openAIRepository)
    }
}

