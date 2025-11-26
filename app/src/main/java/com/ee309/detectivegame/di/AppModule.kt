
package com.ee309.detectivegame.di

import com.ee309.detectivegame.BuildConfig
import com.ee309.detectivegame.llm.client.UpstageApiClient
import com.ee309.detectivegame.llm.client.UpstageApiService
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
    fun provideUpstageApiService(): UpstageApiService {
        val apiKey = BuildConfig.UPSTAGE_API_KEY
        val baseUrl = BuildConfig.UPSTAGE_BASE_URL
        return UpstageApiClient.createService(apiKey, baseUrl)
    }
    
    // TODO: Add Room database provider when database is set up
}
