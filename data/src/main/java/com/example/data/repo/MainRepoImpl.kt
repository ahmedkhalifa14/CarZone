package com.example.data.repo

import android.net.Uri
import com.example.data.local.DataStoreManager
import com.example.data.network.FireBaseService
import com.example.data.network.GeoNamesService
import com.example.domain.entity.Ads
import com.example.domain.entity.GeoNamesResponse
import com.example.domain.entity.ImageEntity
import com.example.domain.entity.User
import com.example.domain.entity.VehicleData
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

//    override suspend fun signInWithGoogle(): Result<GoogleAccountInfo> =
//        firebaseService.signInWithGoogle()


    override suspend fun saveUserData(user: User) {
        firebaseService.saveUserData(user)
    }

    override suspend fun logout() {
    }

    override suspend fun getAllVehiclesCategories(): MutableList<VehiclesCategories> =
        firebaseService.getAllVehiclesCategories()


    override suspend fun saveAsFirstTimeLaunch(isFirstTime: Boolean) {
        dataStoreManager.saveAsFirstTimeLaunch(isFirstTime)
    }

    override suspend fun isFirstTimeLaunch(): Flow<Boolean> =
        dataStoreManager.isFirstTimeLaunch()

    override suspend fun <T : VehicleData> addVehicleAds(ads: Ads<T>) {
        firebaseService.addVehicleAds(ads)
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

    override suspend fun getAllAds(): MutableList<Ads<VehicleData>> {
        return firebaseService.getAllAds()
    }

    override suspend fun fetchRegionsInCountry(
        username: String,
        country: String,
        featureCode: String
    ): GeoNamesResponse =
        geoNamesService.getRegions(username, country, featureCode)


}