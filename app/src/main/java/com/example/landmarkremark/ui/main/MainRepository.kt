package com.example.landmarkremark.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.landmarkremark.models.Locations

object MainRepository {

    private val deviceDetails = MutableLiveData<Locations>()

    fun getLocations(): LiveData<Locations> {
        return deviceDetails
    }

}