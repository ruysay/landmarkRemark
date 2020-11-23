package com.example.landmarkremark.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.landmarkremark.models.LocationData
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import timber.log.Timber
import java.util.*

object MainRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("locations")
    private val locations = MutableLiveData<List<LocationData>>()
    private val searchedLocations = MutableLiveData<List<LocationData>>()

    private var accessToken: String? = null
    private var creatorName: String? = null

    private var firebaseUser: FirebaseUser? = null
    private var baseLocationId: String? = null

    fun init() {
        baseLocationId = dbRef.push().key
        accessToken = SharedPreferenceUtils.getAccessToken()
        creatorName = SharedPreferenceUtils.getEmail()
        firebaseUser = FirebaseAuth.getInstance().currentUser
    }

    fun setLocations(locationDataList: MutableList<LocationData>? = this.locations.value?.toMutableList()) {
        val newLocationList = mutableListOf<LocationData>()
        locationDataList?.let {
            for (locationData: LocationData in it) {
                if (!newLocationList.contains(locationData))
                    newLocationList.add(locationData)
            }
        }
        locations.value = newLocationList
    }

    fun getLocations(): LiveData<List<LocationData>> {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getLocations")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val newLocationList = mutableListOf<LocationData>()
                for (locationSnapShot: DataSnapshot in snapshot.children) {
                    val location = locationSnapShot.getValue(LocationData::class.java)
                    location?.let {
                        // get all public notes and all my private/friendOnly notes
                        if(it.visibility == "public" || (it.creatorId == accessToken && it.visibility != "public")) {
                            newLocationList.add(it)
                        }
                    }
                }
                newLocationList.sortByDescending {
                    it.createdTime
                }
                setLocations(newLocationList)
            }
        })
        return locations
    }

    fun getMyLocations(): MutableList<LocationData>? {
        return locations.value?.filter {
            it.creatorId == accessToken
        }?.toMutableList()
    }

    fun writeNote(
        title: String,
        description: String,
        lat: Double? = -37.8136,
        lng: Double? = 144.9631,
        extra: String? = "",
        visibility: String? = "public",
        imageUrl: String? = ""
    ) {
        accessToken ?: return

        val createdTime = Date().time.toString()
        val location = LocationData(
            title,
            description,
            createdTime,
            accessToken,
            creatorName,
            lat,
            lng,
            extra,
            visibility,
            imageUrl
        )

        baseLocationId?.let {
            dbRef.child(it + createdTime).setValue(location)
        }
        getLocations()
    }

    fun search(keyWord: String? = null) {
        keyWord ?: return

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "search")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val newLocationList = mutableListOf<LocationData>()
                for (locationSnapShot: DataSnapshot in snapshot.children) {
                    val location = locationSnapShot.getValue(LocationData::class.java)
                    location?.let {
                        if (it.visibility == "public" && (it.title?.contains(keyWord) == true ||
                                    it.description?.contains(keyWord) == true ||
                                    it.creatorName == keyWord ||
                                    (it.creatorName?.substring(
                                        0,
                                        it.creatorName.lastIndexOf("@")
                                    ) == keyWord))
                        ) {
                            newLocationList.add(it)
                        }
                    }
                }
                newLocationList.sortByDescending {
                    it.createdTime
                }
                searchedLocations.value = newLocationList
            }
        })
    }

    fun getSearchedLocation(): LiveData<List<LocationData>> {
        return searchedLocations
    }

    fun clear() {
        baseLocationId = null
        accessToken = null
        creatorName = null
        firebaseUser = null
    }
}