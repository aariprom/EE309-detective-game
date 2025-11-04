package com.ee309.detectivegame.di

import android.content.Context
import androidx.room.Room
import com.ee309.detectivegame.llm.client.UpstageApiClient
import com.ee309.detectivegame.llm.client.UpstageApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideUpstageApiService(): UpstageApiService {
        // TODO: Get API key from secure storage (e.g., BuildConfig or local.properties)
        val apiKey = "" // Placeholder - should be retrieved securely
        return UpstageApiClient.createService(apiKey)
    }
    
    // TODO: Add Room database provider when database is set up
}

