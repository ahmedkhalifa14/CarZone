package com.example.carzoneapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carzoneapp.utils.Event
import com.example.carzoneapp.utils.Resource
import com.example.domain.entity.Ad
import com.example.domain.entity.GeoNamesResponse
import com.example.domain.entity.VehicleData
import com.example.domain.entity.VehiclesCategories
import com.example.domain.usecase.AddVehicleAdUseCase
import com.example.domain.usecase.FetchRegionsInCountryUseCase
import com.example.domain.usecase.GetAllAdsUseCase
import com.example.domain.usecase.GetAllVehiclesCategoriesUseCase
import com.example.domain.usecase.IsFirstTimeLaunchUseCase
import com.example.domain.usecase.SaveFirstTimeLaunchUseCase
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
    private val fetchRegionsInCountryUseCase: FetchRegionsInCountryUseCase
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
        MutableStateFlow<Event<Resource<MutableList<Ad<VehicleData>>>>>(Event(Resource.Init()))
    val allAdsState: StateFlow<Event<Resource<MutableList<Ad<VehicleData>>>>> =
        _allAdsState

    private val _fetchRegionsInCountryState =
        MutableStateFlow<Event<Resource<GeoNamesResponse>>>(Event(Resource.Init()))
    val fetchRegionsInCountryState: StateFlow<Event<Resource<GeoNamesResponse>>> =
        _fetchRegionsInCountryState


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

//    fun addVehicleAds(ads: Ads<VehicleData>) {
//        viewModelScope.launch(Dispatchers.Main) {
//            _addVehicleAdsState.emit(Event(Resource.Loading()))
//            try {
//                addVehicleAdsUseCase(ads)
//                _addVehicleAdsState.emit(Event(Resource.Success("Success")))
//            } catch (e: Exception) {
//                _addVehicleAdsState.emit(Event(Resource.Error(e.message.toString())))
//            }
//        }
//    }


    fun addVehicleAd(ads: Ad<VehicleData>) {
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
}