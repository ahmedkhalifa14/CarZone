package com.example.domain.repo

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.domain.entity.Ad
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.GeoNamesResponse
import com.example.domain.entity.ImageEntity
import com.example.domain.entity.User
import com.example.domain.entity.UserChat
import com.example.domain.entity.VehiclesCategories
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface MainRepo {
    suspend fun register(email: String, password: String)
    suspend fun login(email: String, password: String)

    //suspend fun signInWithGoogle(): Result<GoogleAccountInfo>
    suspend fun saveUserData(user: User)
    suspend fun logout()
    suspend fun getAllVehiclesCategories(): MutableList<VehiclesCategories>
    suspend fun saveAsFirstTimeLaunch(isFirstTime: Boolean)
    suspend fun isFirstTimeLaunch(): Flow<Boolean>

    // suspend fun addVehicleAds(vehicle: Vehicle<Car>)
    suspend fun addVehicleAds(ads: Ad)
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun sendVerificationCode(phoneNumber: String): String
    suspend fun verifyCode(verificationId: String, code: String)


    suspend fun uploadImages(imageUris: List<Uri>): List<String>
    suspend fun saveImageUrl(imageUrl: String)
    suspend fun getImages(): List<ImageEntity>
    suspend fun getAllAds(): MutableList<Ad>
    suspend fun fetchRegionsInCountry(
        username: String,
        country: String,
        featureCode: String
    ): GeoNamesResponse

    suspend fun getUserByUserId(userId: String): User
    suspend fun getAllAdsByVehicleType(targetVehicleType: String): List<Ad>
    suspend fun getAllAdsByUserId(userId: String): List<Ad>
    suspend fun sendMessage(message: ChatMessage)
    fun getMessages(receiverID: String, senderID: String): LiveData<List<ChatMessage>>

    suspend fun saveUserChats(userChat: UserChat)
    suspend fun getChatMessages(userId: String): List<UserChat>


}