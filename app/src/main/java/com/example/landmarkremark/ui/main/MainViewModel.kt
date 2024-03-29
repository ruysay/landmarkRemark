package com.example.landmarkremark.ui.main

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.interfaces.FBUserTaskOnCompleteListener
import com.example.landmarkremark.models.LocationData

class MainViewModel : ViewModel() {

    private val repository = MainRepository

    fun init() {
        repository.init()
    }

    fun signOut() {
        repository.signOut()
    }

    fun setLocations(locationData: MutableList<LocationData>? = getLocations().value?.toMutableList()) {
        repository.setLocations(locationData)
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
    ): LocationData? {
        return repository.writeNote(title, description, lat, lng, extra, visibility, imageUrl)
    }

    fun search(keyWord: String? = null) {
        return repository.search(keyWord)
    }

    fun getSearchedLocation(): LiveData<List<LocationData>> {
        return repository.getSearchedLocation()
    }

    fun createUserWithEmailAndPassword(email: String, password: String, activity: Activity, listener: FBUserTaskOnCompleteListener) {
        repository.createUserWithEmailAndPassword(email, password, activity, listener)
    }

}