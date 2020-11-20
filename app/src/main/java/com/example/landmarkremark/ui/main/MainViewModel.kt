package com.example.landmarkremark.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.models.LocationData

class MainViewModel : ViewModel() {

    private val repository = MainRepository

    fun setLocations(locationData: MutableList<LocationData>? = getLocations().value?.toMutableList()) {
        repository.setLocations()
    }

    fun getLocations() : LiveData<List<LocationData>> {
        return repository.getLocations()
    }

    fun getMyLocations() : MutableList<LocationData>? {
        return repository.getMyLocations()
    }

    fun writeNote() {
        return repository.writeNote()
    }
}