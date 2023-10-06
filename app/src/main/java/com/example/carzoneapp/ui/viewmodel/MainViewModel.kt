package com.example.carzoneapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carzoneapp.utils.Event
import com.example.carzoneapp.utils.Resource
import com.example.domain.entity.Ad
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.GeoNamesResponse
import com.example.domain.entity.LaunchInfo
import com.example.domain.entity.SavedItem
import com.example.domain.entity.User
import com.example.domain.entity.UserChat
import com.example.domain.entity.VehiclesCategories
import com.example.domain.usecase.AddToSavedItemsUseCase
import com.example.domain.usecase.AddVehicleAdUseCase
import com.example.domain.usecase.FetchRegionsInCountryUseCase
import com.example.domain.usecase.GetAllAdsByUserIdUseCase
import com.example.domain.usecase.GetAllAdsByVehicleTypeUseCase
import com.example.domain.usecase.GetAllAdsUseCase
import com.example.domain.usecase.GetAllVehiclesCategoriesUseCase
import com.example.domain.usecase.GetMessagesUseCase
import com.example.domain.usecase.GetSavedItemsByUserIdUseCase
import com.example.domain.usecase.GetUserChatListUseCase
import com.example.domain.usecase.GetUserInfoUseCase
import com.example.domain.usecase.IsFirstTimeLaunchUseCase
import com.example.domain.usecase.RemoveFromSavedItemsByUserIdUseCase
import com.example.domain.usecase.SaveFirstTimeLaunchUseCase
import com.example.domain.usecase.SaveUserChatListUseCase
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
class MainViewModel @Inject constructor(
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
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val getUserChatListUseCase: GetUserChatListUseCase,
    private val saveUserChatListUseCase: SaveUserChatListUseCase,
    private val removeFromSavedItemsByUserIdUseCase: RemoveFromSavedItemsByUserIdUseCase,
    private val addToSavedItemsUseCase: AddToSavedItemsUseCase,
    private val getSavedItemsByUserIdUseCase: GetSavedItemsByUserIdUseCase
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
        MutableStateFlow<Event<Resource<Flow<LaunchInfo>>>>(Event(Resource.Init()))
    val isFirstTimeLaunchState: StateFlow<Event<Resource<Flow<LaunchInfo>>>> =
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

    private val _getMessagesState =
        MutableStateFlow<Event<Resource<LiveData<List<ChatMessage>>>>>(Event(Resource.Init()))
    val getMessagesState: StateFlow<Event<Resource<LiveData<List<ChatMessage>>>>> =
        _getMessagesState

    private val _saveUserChatListState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val saveUserChatListState: StateFlow<Event<Resource<String>>> =
        _saveUserChatListState

    private val _getUserChatListState =
        MutableStateFlow<Event<Resource<List<UserChat>>>>(Event(Resource.Init()))
    val getUserChatListState: StateFlow<Event<Resource<List<UserChat>>>> =
        _getUserChatListState

    private val _addToSavedItemsState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val addToSavedItemsState: StateFlow<Event<Resource<String>>> =
        _addToSavedItemsState

    private val _removeSavedItemsState =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.Init()))
    val removeSavedItemsState: StateFlow<Event<Resource<String>>> =
        _removeSavedItemsState

    private val _getSavedItemsState =
        MutableStateFlow<Event<Resource<List<SavedItem>>>>(Event(Resource.Init()))
    val getSavedItemsState: StateFlow<Event<Resource<List<SavedItem>>>> =
        _getSavedItemsState


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

    fun sendMessage(message: ChatMessage) {
        viewModelScope.launch(Dispatchers.Main) {
            _sendMessageState.emit(Event(Resource.Loading()))
            try {
                sendMessageUseCase(message)
                _sendMessageState.emit(Event(Resource.Success("send Successfully")))
            } catch (e: Exception) {
                _sendMessageState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun getMessages(receiverID: String, senderID: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _getMessagesState.emit(Event(Resource.Loading()))
            try {
                val messages = getMessagesUseCase(receiverID, senderID)
                _getMessagesState.emit(Event(Resource.Success(messages)))
            } catch (e: Exception) {
                _getMessagesState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }


    fun saveUserChatList(userChat: UserChat) {
        viewModelScope.launch(Dispatchers.Main) {
            _saveUserChatListState.emit(Event(Resource.Loading()))
            try {
                saveUserChatListUseCase(userChat)
                _saveUserChatListState.emit(Event(Resource.Success("saved Successfully")))
            } catch (e: Exception) {
                _saveUserChatListState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun getUserChatList(userId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _getUserChatListState.emit(Event(Resource.Loading()))
            try {
                val chats = getUserChatListUseCase(userId)
                _getUserChatListState.emit(Event(Resource.Success(chats)))
            } catch (e: Exception) {
                _getUserChatListState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun saveFirstTimeLaunch(launchInfo: LaunchInfo) {
        viewModelScope.launch(Dispatchers.Main) {
            _saveFirstTimeLaunchState.emit(Event(Resource.Loading()))
            try {
                saveFirstTimeLaunchUseCase(launchInfo)
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

    fun getAllAds(loading: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            if (loading) _allAdsState.emit(Event(Resource.Loading()))
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

    fun addToSavedItems(savedItem: SavedItem) {
        viewModelScope.launch(Dispatchers.Main) {
            _addToSavedItemsState.emit((Event(Resource.Loading())))
            try {
                addToSavedItemsUseCase(savedItem)
                _addToSavedItemsState.emit(Event(Resource.Success(savedItem.itemId)))
            } catch (e: Exception) {
                _addToSavedItemsState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun getSavedItemsByUserId(userId: String, loading: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            if (loading) _getSavedItemsState.emit((Event(Resource.Loading())))
            try {
                val savedItems = getSavedItemsByUserIdUseCase(userId)
                _getSavedItemsState.emit(Event(Resource.Success(savedItems)))
            } catch (e: Exception) {
                _getSavedItemsState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }

    fun removeFromSavedItemsByUserId(userId: String, itemId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _removeSavedItemsState.emit((Event(Resource.Loading())))
            try {
                removeFromSavedItemsByUserIdUseCase(userId, itemId)
                _removeSavedItemsState.emit(Event(Resource.Success(itemId)))
            } catch (e: Exception) {
                _removeSavedItemsState.emit(Event(Resource.Error(e.message.toString())))
            }
        }
    }


}