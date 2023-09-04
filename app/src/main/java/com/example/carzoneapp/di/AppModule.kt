package com.example.carzoneapp.di

import android.content.Context
import com.example.data.local.DataStoreManager
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
    fun provideApplicationContext(
        @ApplicationContext applicationContext: ApplicationContext
    ) = applicationContext

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context):DataStoreManager=
        DataStoreManager(context)


}