package com.example.carzoneapp.di

import com.example.data.local.DataStoreManager
import com.example.data.network.FireBaseService
import com.example.data.network.GeoNamesService
import com.example.data.repo.MainRepoImpl
import com.example.domain.repo.MainRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    @Singleton
    fun provideMainRepo(
        firebaseService: FireBaseService,
        dataStoreManager: DataStoreManager,
        geoNamesService: GeoNamesService
    ): MainRepo =
        MainRepoImpl(firebaseService, dataStoreManager,geoNamesService)

}