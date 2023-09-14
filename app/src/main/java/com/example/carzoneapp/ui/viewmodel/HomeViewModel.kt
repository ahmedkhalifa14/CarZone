package com.example.carzoneapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carzoneapp.utils.Event
import com.example.carzoneapp.utils.Resource
import com.example.domain.entity.Ad
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.GeoNamesResponse
import com.example.domain.entity.User
import com.example.domain.entity.VehiclesCategories
import com.example.domain.usecase.AddVehicleAdUseCase
import com.example.domain.usecase.FetchRegionsInCountryUseCase
import com.example.domain.usecase.GetAllAdsByUserIdUseCase
import com.example.domain.usecase.GetAllAdsByVehicleTypeUseCase
import com.example.domain.usecase.GetAllAdsUseCase
import com.example.domain.usecase.GetAllVehiclesCategoriesUseCase
import com.example.domain.usecase.GetUserInfoUseCase
import com.example.domain.usecase.IsFirstTimeLaunchUseCase
import com.example.domain.usecase.SaveFirstTimeLaunchUseCase
import com.example.domain.usecase.SendMessageUseCase
import com.example.domain.usecase.UploadImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllVehiclesCategoriesUseCase: GetAllVehiclesCategoriesUseCase,
    private val saveFirstTimeLaunchUseCase: SaveFirstTimeLaunchUseCase,
    private val isFirstTimeLaunchUseCase: IsFirstTimeLaunchUseCase,
    private val addVehicleAdsUseCase: AddVehicleAdUseCase,
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val getAllAdsUseCase: GetAllAdsUseCase,
    private val fetchRegionsInCountryUseCase: FetchRegionsInCountryUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getAllAdsByVehicleTypeUseCase: GetAllAdsByVehicleTypeUseCase,
    private val getAllAdsByUserIdUseCase: GetAllAdsByUserIdUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _vehiclesCategoriesState =
        MutableStateFlow<Event<Resource<MutableList<VehiclesCategories>>>>(Event(Resource.Init()))
    val vehiclesCategoriesState: StateFlow<Event<Resource<MutableList<VehiclesCategories>>>> =
        _vehiclesCategoriesState

    private val _addVehicleAdsState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val addVehicleAdsState: StateFlow<Event<Resource<String>>> =
        _addVehicleAdsState


    private val _uploadImagesState =
        MutableStateFlow<Event<Resource<List<String>>>>(Event(Resource.Init()))
    val uploadImagesState: StateFlow<Event<Resource<List<String>>>> =
        _uploadImagesState

    private val _saveFirstTimeLaunchState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val saveFirstTimeLaunchState: StateFlow<Event<Resource<String>>> =
        _saveFirstTimeLaunchState

    private val _isFirstTimeLaunchState =
        MutableStateFlow<Event<Resource<Flow<Boolean>>>>(Event(Resource.Init()))
    val isFirstTimeLaunchState: StateFlow<Event<Resource<Flow<Boolean>>>> =
        _isFirstTimeLaunchState

    private val _allAdsState =
        MutableStateFlow<Event<Resource<MutableList<Ad>>>>(Event(Resource.Init()))
    val allAdsState: StateFlow<Event<Resource<MutableList<Ad>>>> =
        _allAdsState

    private val _fetchRegionsInCountryState =
        MutableStateFlow<Event<Resource<GeoNamesResponse>>>(Event(Resource.Init()))
    val fetchRegionsInCountryState: StateFlow<Event<Resource<GeoNamesResponse>>> =
        _fetchRegionsInCountryState
    private val _userInfoState =
        MutableStateFlow<Event<Resource<User>>>(Event(Resource.Init()))
    val userInfoState: StateFlow<Event<Resource<User>>> =
        _userInfoState


    private val _allAdsByVehicleTypeState =
        MutableStateFlow<Event<Resource<List<Ad>>>>(Event(Resource.Init()))
    val allAdsByVehicleTypeState: StateFlow<Event<Resource<List<Ad>>>> =
        _allAdsByVehicleTypeState


    private val _userAdsState =
        MutableStateFlow<Event<Resource<List<Ad>>>>(Event(Resource.Init()))
    val userAdsState: StateFlow<Event<Resource<List<Ad>>>> =
        _userAdsState



    private val _sendMessageState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val sendMessageState: StateFlow<Event<Resource<String>>> =
        _sendMessageState


    fun isFirstTimeLaunch() {
        viewModelScope.launch(Dispatchers.Main) {
            _isFirstTimeLaunchState.emit(Event(Resource.Loading()))
            try {
                val result = isFirstTimeLaunchUseCase()
                _isFirstTimeLaunchState.emit(Event(Resource.Success(result)))

            } catch (e: Exception) {
                _isFirstTimeLaunchState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }
    fun sendMessage(message: ChatMessage){
        viewModelScope.launch (Dispatchers.Main){
            _sendMessageState.emit(Event(Resource.Loading()))
            try {
                sendMessageUseCase(message)
                _sendMessageState.emit(Event(Resource.Success("send Successfully")))
            }catch (e:Exception){
                _sendMessageState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun saveFirstTimeLaunch(isFirstTimeLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _saveFirstTimeLaunchState.emit(Event(Resource.Loading()))
            try {
                saveFirstTimeLaunchUseCase(isFirstTimeLaunch)
                _saveFirstTimeLaunchState.emit(Event(Resource.Success("Success")))
            } catch (e: Exception) {
                _saveFirstTimeLaunchState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun addVehicleAd(ads: Ad) {
        viewModelScope.launch(Dispatchers.Main) {
            _addVehicleAdsState.emit(Event(Resource.Loading()))
            try {
                addVehicleAdsUseCase(ads)
                _addVehicleAdsState.emit(Event(Resource.Success("Success")))
            } catch (e: Exception) {
                _addVehicleAdsState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun fetchRegionsInCountry(username: String, country: String, featureCode: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _fetchRegionsInCountryState.emit(Event(Resource.Loading()))
            try {
                val geoNamesList = fetchRegionsInCountryUseCase(username, country, featureCode)
                _fetchRegionsInCountryState.emit(Event(Resource.Success(geoNamesList)))
            } catch (e: Exception) {
                _fetchRegionsInCountryState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun getAllVehiclesCategories() {
        viewModelScope.launch(Dispatchers.Main) {
            _vehiclesCategoriesState.emit(Event(Resource.Loading()))
            try {
                val result = getAllVehiclesCategoriesUseCase()
                _vehiclesCategoriesState.emit(Event(Resource.Success(result)))
            } catch (e: Exception) {
                _vehiclesCategoriesState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun getAllAds() {
        viewModelScope.launch(Dispatchers.Main) {
            _allAdsState.emit(Event(Resource.Loading()))
            try {
                val allAds = getAllAdsUseCase()
                _allAdsState.emit(Event(Resource.Success(allAds)))
            } catch (e: Exception) {
                _allAdsState.emit(Event(Resource.Error(e.message.toString())))

            }
        }
    }


    fun getAdsByVehicleType(vehicleType: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _allAdsByVehicleTypeState.emit(Event(Resource.Loading()))
            try {
                val ads = getAllAdsByVehicleTypeUseCase(vehicleType)
                _allAdsByVehicleTypeState.emit(Event(Resource.Success(ads)))
            } catch (e: Exception) {
                _allAdsByVehicleTypeState.emit(Event(Resource.Error(e.message.toString())))

            }
        }
    }


    fun getUserAds(userId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _userAdsState.emit(Event(Resource.Loading()))
            try {
                val ads = getAllAdsByUserIdUseCase(userId)
                _userAdsState.emit(Event(Resource.Success(ads)))
            } catch (e: Exception) {
                _userAdsState.emit(Event(Resource.Error(e.message.toString())))

            }
        }
    }

    fun uploadImages(imageUris: List<Uri>) {
        viewModelScope.launch(Dispatchers.Main) {
            _uploadImagesState.emit(Event((Resource.Loading())))
            try {
                val imagesUrls = uploadImagesUseCase(imageUris)
                _uploadImagesState.emit(Event(Resource.Success(imagesUrls)))
            } catch (e: Exception) {
                _uploadImagesState.emit(Event(Resource.Error(e.message.toString())))

            }

        }
    }

    fun getUserInfoByUserId(userId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _userInfoState.emit((Event(Resource.Loading())))
            try {
                val userData = getUserInfoUseCase(userId)
                _userInfoState.emit(Event(Resource.Success(userData)))
            } catch (e: Exception) {
                _userInfoState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }
}