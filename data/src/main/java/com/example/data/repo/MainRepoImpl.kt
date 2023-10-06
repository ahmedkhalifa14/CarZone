package com.example.data.repo

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.data.local.DataStoreManager
import com.example.data.network.FireBaseService
import com.example.data.network.GeoNamesService
import com.example.domain.entity.Ad
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.GeoNamesResponse
import com.example.domain.entity.ImageEntity
import com.example.domain.entity.LaunchInfo
import com.example.domain.entity.SavedItem
import com.example.domain.entity.User
import com.example.domain.entity.UserChat
import com.example.domain.entity.VehiclesCategories
import com.example.domain.repo.MainRepo
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepoImpl @Inject constructor(
    private val firebaseService: FireBaseService,
    private val dataStoreManager: DataStoreManager,
    private val geoNamesService: GeoNamesService
) : MainRepo {
    override suspend fun register(email: String, password: String) {
        firebaseService.register(email, password)
    }

    override suspend fun login(email: String, password: String) {
        firebaseService.loginWithEmail(email, password)
    }

    override suspend fun saveUserData(user: User) {
        firebaseService.saveUserData(user)
    }

    override suspend fun logout() {
    }

    override suspend fun getAllVehiclesCategories(): MutableList<VehiclesCategories> =
        firebaseService.getAllVehiclesCategories()


    override suspend fun saveAsFirstTimeLaunch(launchInfo: LaunchInfo) {
        dataStoreManager.saveAsFirstTimeLaunch(launchInfo)
    }

    override suspend fun isFirstTimeLaunch(): Flow<LaunchInfo> =
        dataStoreManager.isFirstTimeLaunch()

    override suspend fun addVehicleAds(ad: Ad) {
        firebaseService.addVehicleAds(ad)
    }


    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return firebaseService.signInWithGoogle(idToken)!!
    }

    override suspend fun sendVerificationCode(phoneNumber: String): String {
        return firebaseService.sendVerificationCode(phoneNumber)
    }

    override suspend fun verifyCode(verificationId: String, code: String) {
        return firebaseService.verifyCode(verificationId, code)
    }

    override suspend fun uploadImages(imageUris: List<Uri>): List<String> {
        return firebaseService.uploadImages(imageUris)
    }

    override suspend fun saveImageUrl(imageUrl: String) {
        firebaseService.saveImageUrl(imageUrl)
    }

    override suspend fun getImages(): List<ImageEntity> {
        val imageUrls = firebaseService.getImages()
        return imageUrls.map { ImageEntity(it) }
    }

    override suspend fun getAllAds(): MutableList<Ad> {
        return firebaseService.getAllAds()
    }

    override suspend fun fetchRegionsInCountry(
        username: String,
        country: String,
        featureCode: String
    ): GeoNamesResponse =
        geoNamesService.getRegions(username, country, featureCode)

    override suspend fun getUserByUserId(userId: String): User =
        firebaseService.getUserByUserId(userId)!!

    override suspend fun getAllAdsByVehicleType(targetVehicleType: String): List<Ad> =
        firebaseService.getAllAdsByVehicleType(targetVehicleType)

    override suspend fun getAllAdsByUserId(userId: String): List<Ad> =
        firebaseService.getUserAds(userId)

    override suspend fun sendMessage(message: ChatMessage) {
        firebaseService.sendMessage(message)
    }

    override fun getMessages(receiverID: String, senderID: String): LiveData<List<ChatMessage>> =
        firebaseService.getMessages(receiverID, senderID)

    override suspend fun saveUserChats(userChat: UserChat) {
        firebaseService.saveUserChats(userChat)
    }

    override suspend fun getChatMessages(userId: String): List<UserChat> =
        firebaseService.getUserChats(userId)

    override suspend fun addToSavedItems(savedItem: SavedItem) =
        firebaseService.addToSavedItems(savedItem)

    override suspend fun getSavedItemsByUserId(userId: String): List<SavedItem> =
        firebaseService.getSavedItemsByUserId(userId)

    override suspend fun removeFromSavedItemsByUserId(userId: String, itemId: String) =
        firebaseService.removeFromSavedItemsByUserId(userId, itemId)

}