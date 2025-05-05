package com.example.mangavision.di

import android.content.Context
import androidx.room.Room
import com.example.mangavision.data.local.MangaVisionDatabase
import com.example.mangavision.data.local.dao.MangaDao
import com.example.mangavision.data.local.dao.UserDao
import com.example.mangavision.data.remote.api.MangaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MangaVisionDatabase {
        return Room.databaseBuilder(
            context,
            MangaVisionDatabase::class.java,
            "mangavision.db"
        )
        .addMigrations(MangaVisionDatabase.MIGRATION_1_2)
        .build()
    }

    @Provides
    fun provideUserDao(database: MangaVisionDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideMangaDao(database: MangaVisionDatabase): MangaDao {
        return database.mangaDao()
    }

    @Provides
    @Singleton
    fun provideMangaApi(): MangaApiService {
        return Retrofit.Builder()
            .baseUrl("https://mangaverse-api.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MangaApiService::class.java)
    }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}