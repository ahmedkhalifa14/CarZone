package com.example.data.network

import com.example.domain.entity.GeoNamesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {
    @GET("searchJSON")
    suspend fun getRegions(
        @Query("username") username: String,
        @Query("q") country: String,
        @Query("featureCode") featureCode: String = "ADM1"
    ): GeoNamesResponse
}