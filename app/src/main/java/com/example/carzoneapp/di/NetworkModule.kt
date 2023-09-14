package com.example.carzoneapp.di

import com.example.data.network.FireBaseService
import com.example.data.network.GeoNamesService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFireBaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseService(auth: FirebaseAuth, fireStore: FirebaseFirestore,firebaseStorage:FirebaseStorage,firebaseDatabase:FirebaseDatabase)
    : FireBaseService =
        FireBaseService(auth, fireStore,firebaseStorage,firebaseDatabase)

    @Provides
    @Singleton
    fun provideFireStore()= FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage()= FirebaseStorage.getInstance()


    @Provides
    @Singleton
    fun provideFireBaseDataBase()= FirebaseDatabase.getInstance()


    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://api.geonames.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): GeoNamesService {
        return retrofit.create(GeoNamesService::class.java)
    }


}