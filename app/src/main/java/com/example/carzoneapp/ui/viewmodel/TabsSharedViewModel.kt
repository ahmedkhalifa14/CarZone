package com.example.carzoneapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TabsSharedViewModel : ViewModel() {
    private val specificationsData = MutableLiveData<String>()

    fun setSpecificationsData(data: String) {
        specificationsData.value = data
    }

    fun getSpecificationsData(): LiveData<String> {
        return specificationsData
    }
}