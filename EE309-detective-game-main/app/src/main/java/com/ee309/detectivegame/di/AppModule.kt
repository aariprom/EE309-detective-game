package com.ee309.detectivegame.di

import com.ee309.detectivegame.BuildConfig
import com.ee309.detectivegame.llm.client.OpenAiApiClient
import com.ee309.detectivegame.llm.client.OpenAiApiService
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
    fun provideOpenAiApiService(): OpenAiApiService {
        val apiKey = BuildConfig.OPENAI_API_KEY
        return OpenAiApiClient.createService(apiKey)
    }
}
