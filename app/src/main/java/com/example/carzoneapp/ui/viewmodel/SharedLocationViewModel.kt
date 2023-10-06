package com.example.carzoneapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedLocationViewModel @Inject constructor() : ViewModel() {
    private val selectedLocationLiveData = MutableLiveData<String>()

    fun setSelectedLocation(location: String) {
        selectedLocationLiveData.value = location
    }

    fun getSelectedLocationLiveData(): LiveData<String> {
        return selectedLocationLiveData
    }
}
