package com.example.landmarkremark.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.landmarkremark.models.Locations
import com.example.landmarkremark.ui.main.MainRepository

class MainViewModel : ViewModel() {

    private val repository = MainRepository

    fun getLocations() : LiveData<Locations> {
        return repository.getLocations()
    }
}