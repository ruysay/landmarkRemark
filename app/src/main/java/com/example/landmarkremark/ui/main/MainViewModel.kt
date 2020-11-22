package com.example.landmarkremark.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.models.LocationData
import com.google.android.gms.maps.model.LatLng

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

    fun writeNote(
          title: String,
          description: String,
          lat: Double? = 0.0,
          lng: Double? = 0.0,
          extra: String? = "",
          visibility: String? = "public",
          imageUrl: String? = ""
    ) {
        return repository.writeNote(title, description, lat, lng, extra, visibility, imageUrl)
    }

    fun search(keyWord: String? = null) {
        return repository.search(keyWord)
    }

    fun getSearchedLocation(): LiveData<List<LocationData>> {
        return repository.getSearchedLocation()
    }

}