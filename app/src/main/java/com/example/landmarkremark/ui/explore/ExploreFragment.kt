package com.example.landmarkremark.ui.explore

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landmarkremark.R
import com.example.landmarkremark.interfaces.RecyclerViewListener
import com.example.landmarkremark.models.LocationData
import com.example.landmarkremark.ui.add.AddLocationNoteActivity
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainActivity.Companion.ADD_LOCATION_NOTE_REQUEST_CODE
import com.example.landmarkremark.ui.main.MainViewModel
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_explore.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class ExploreFragment : Fragment(), LocationListener, RecyclerViewListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val MIN_TIME_FOR_UPDATE: Long = 5000
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATE: Float = 0f
    }

    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker: Marker? = null

    private lateinit var mainViewModel: MainViewModel

    private var isGpsEnabled: Boolean? = false
    private var isNetworkEnabled: Boolean? = false
    private lateinit var locationManager: LocationManager

    private var haveAskedPermission = false
    private var haveSetUI = false

    private var myLocation: LatLng? = null
    private var myAddress: String? = null

    private lateinit var locationInfoContainer: CardView
    private lateinit var locationInfoFold: ImageView
    private lateinit var locationInfoImage: ImageView
    private lateinit var locationInfoTitle: TextView
    private lateinit var locationInfoDescription: TextView
    private lateinit var locationInfoDescriptionAdd: TextView
    private lateinit var locationInfoCreator: TextView
    private lateinit var locationInfoCreatedTime: TextView


    private val searchAdapter: SearchResultAdapter by lazy {
        SearchResultAdapter(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as MainActivity
        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
    }

    override fun onStop() {
        super.onStop()
        explore_location_map.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    private var isTouchInfoFold = false
    private var searchKeyWord: String? = null

    private val searchCountdownTimer = object : CountDownTimer(300, 300) {
        override fun onFinish() {
            if (searchKeyWord.isNullOrBlank()) {
                searchAdapter.setList(mutableListOf())
                explore_location_search_recycler_view.visibility = GONE
            } else {
                Timber.d("checkSearch - searchKeyWord:$searchKeyWord")
                mainViewModel.search(searchKeyWord)
                searchKeyWord = null
            }
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(context, Locale.getDefault())
        explore_location_map.onCreate(savedInstanceState)

        locationInfoContainer = view.findViewById(R.id.explore_location_info_container)
        locationInfoFold = view.findViewById(R.id.explore_location_info_fold)
        locationInfoImage = view.findViewById(R.id.explore_location_info_image)
        locationInfoTitle = view.findViewById(R.id.explore_location_info_title)
        locationInfoDescription = view.findViewById(R.id.explore_location_info_description)
        locationInfoDescriptionAdd = view.findViewById(R.id.explore_location_info_add)
        locationInfoCreator = view.findViewById(R.id.explore_location_info_creator_name)
        locationInfoCreatedTime = view.findViewById(R.id.explore_location_info_created_time)

        mainViewModel.getLocations().observe(viewLifecycleOwner, Observer {
            if (::map.isInitialized) {
                updateMapWithNotes(it)
            } else {
                mainViewModel.getLocations()
                mainViewModel.getMyLocations()
            }
        })

        mainViewModel.getSearchedLocation().observe(viewLifecycleOwner, Observer {
            Timber.d("getSearchedLocation - search")
            if (it.isNotEmpty()) {
                searchAdapter.setList(it.toMutableList())
                explore_location_search_recycler_view.visibility = VISIBLE
            } else {
                explore_location_search_recycler_view.visibility = GONE
            }
        })

        setSearchBarUI()
        setLocationInfoUI()
        setFab()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMapUI()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        explore_location_map.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        explore_location_map.onResume()

        val context = this.context ?: return
        if (!haveSetUI) {
            haveSetUI = true
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || !haveAskedPermission) {
                    haveAskedPermission = true
                    showPermissionDialog(context)
                }
            } else {
                showMapUI()
            }
        }
    }

    private fun updateMapWithNotes(locations: List<LocationData>) {
        val accessToken = SharedPreferenceUtils.getUserId()
        if (::map.isInitialized) {
            for (data: LocationData in locations) {
                if (data.lat != null && data.lng != null) {
                    val markOptions = MarkerOptions()
                    if (data.creatorId == accessToken) {
                        markOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                        )
                    } else {
                        markOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_ORANGE
                            )
                        )
                    }
                    markOptions.title(data.title!!).position(LatLng(data.lat, data.lng))
                    map.addMarker(markOptions)
                }
            }
        }
    }

    private fun showMapUI() {
        val context = this.context ?: return

        TransitionManager.beginDelayedTransition(search_location_container)
        explore_location_map.visibility = VISIBLE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        MapsInitializer.initialize(context)

        explore_location_map.getMapAsync {
            map = it
            map.setOnMapClickListener { latLng ->
                myLocation = latLng
                showLocationInfo(null, true)
            }
            map.setOnMapLongClickListener { latLng ->
                updateLocation(latLng)
            }
            map.setOnMarkerClickListener { marker ->
                mainViewModel.getLocations().value?.firstOrNull() { locationData ->
                    locationData.title == marker.title
                }.let { data ->
                    showLocationInfo(data, showAdd = data == null)
                }
                false
            }
            map.setOnInfoWindowClickListener { marker ->
                mainViewModel.getLocations().value?.firstOrNull() { locationData ->
                    locationData.title == marker.title
                }.let { data ->
                    showLocationInfo(data, showAdd = data == null)
                }
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Location may be null
                try {
                    if (location == null) {
                        var lastLocation = location
                        when {
                            // Use GPS first if available
                            isGpsEnabled == true -> {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    if (::locationManager.isInitialized) {
                                        //requestLocationUpdates
                                        locationManager.requestLocationUpdates(
                                            LocationManager.GPS_PROVIDER,
                                            MIN_TIME_FOR_UPDATE,
                                            MIN_DISTANCE_CHANGE_FOR_UPDATE,
                                            this
                                        )
                                    }
                                    // use locationManager's last known location
                                    lastLocation = locationManager.getLastKnownLocation(
                                        LocationManager.GPS_PROVIDER
                                    )
                                    Timber.d("lastLocation from GPS: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
                                }
                            }

                            isNetworkEnabled == true -> {
                                if (::locationManager.isInitialized) {
                                    //requestLocationUpdates
                                    locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this
                                    )
                                }
                                // use locationManager's last known location
                                lastLocation =
                                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                Timber.d("lastLocation from NETWORK: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
                            }
                        }
                        if (lastLocation != null) {
                            updateLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
                        } else {
                            updateLocation(LatLng(-37.81, 144.96))
                        }

                    } else {
                        Timber.d("lastLocation from fusedLocationClient: ${location?.latitude}, ${location?.longitude}")
                        updateLocation(LatLng(location.latitude, location.longitude))
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }


    private fun updateLocation(latLng: LatLng) {
        if (::map.isInitialized) {
            marker?.remove()
            marker =
                map.addMarker(MarkerOptions().position(latLng).title(getString(R.string.title_new_location)))
            marker?.isVisible = true
            val zoom = if(map.cameraPosition.zoom > 10F) map.cameraPosition.zoom else 10F
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(
                        latLng
                    ).zoom(zoom).tilt(30F).build()
                )
            )
        }
        myLocation = latLng
    }

    override fun onLocationChanged(location: Location) {
        updateLocation(LatLng(location.latitude, location.longitude))
    }

    private fun showPermissionDialog(context: Context) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.location_permission_alert_title)
        dialog.setMessage(R.string.location_permission_alert_message)
        dialog.setPositiveButton(R.string.common_confirm) { _, _ ->
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        dialog.setNegativeButton(R.string.common_cancel) { _, _ ->
            Toast.makeText(context, R.string.location_permission_alert_message, Toast.LENGTH_LONG).show()
            showPermissionDialog(context)
        }
        dialog.show()
    }

    override fun onRecyclerViewItemClickListener(arg1: Any?, arg2: Any?, arg3: Any?) {
        showLocationInfo(arg1 as LocationData)
    }

    fun showLocationInfo(locationData: LocationData? = null, showAdd: Boolean? = false) {
        var addressFromLatLng = mutableListOf<Address>()
        if (locationData == null) {
            myLocation?.let {
                updateLocation(LatLng(it.latitude, it.longitude))
                addressFromLatLng = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            }
            locationInfoTitle.text = getString(R.string.title_new_location)
            locationInfoDescription.text = getString(R.string.msg_un_noted_location)
            locationInfoCreator.visibility = GONE
            locationInfoCreatedTime.visibility = GONE
        } else {
            updateLocation(LatLng(locationData.lat!!, locationData.lng!!))
            addressFromLatLng = geocoder.getFromLocation(locationData.lat, locationData.lng, 1)
            locationInfoTitle.text = locationData.title
            locationInfoDescription.text = locationData.description

            val creatorTxt = getString(R.string.creator_name_txt, locationData.creatorName ?: "")
            locationInfoCreator.text = creatorTxt
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            locationData.createdTime?.let {
                val nowTime = dateFormat.format(Date(locationData.createdTime.toLong()))
                locationInfoCreatedTime.text = nowTime
            }
            locationInfoCreator.visibility = VISIBLE
            locationInfoCreatedTime.visibility = VISIBLE
        }
        if (!addressFromLatLng.isNullOrEmpty()) {
            myAddress = addressFromLatLng[0].getAddressLine(0)
            locationInfoDescription.text = locationInfoDescription.text.toString() + "\n$myAddress"
        }

        locationInfoDescriptionAdd.visibility = if (showAdd == true) VISIBLE else GONE
        locationInfoContainer.animate().alpha(1f).translationY(0f).duration = 250
        locationInfoContainer.visibility = VISIBLE
    }

    private fun hideLocationInfo() {
        locationInfoTitle.text = null
        locationInfoDescription.text = null
        locationInfoContainer.visibility = GONE
    }

    private fun setSearchBarUI() {
        explore_location_search_recycler_view.adapter = searchAdapter
        explore_location_search_recycler_view.layoutManager = LinearLayoutManager(context)
        explore_location_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                searchKeyWord = query
                mainViewModel.search(searchKeyWord)
                searchKeyWord = null
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                //Start filtering the list as user start entering the characters
                searchKeyWord = query
                searchCountdownTimer.cancel()
                searchCountdownTimer.start()
                return false
            }
        })
    }

    private fun setFab() {
        //set Fab
        explore_location_fab_search.setOnClickListener {
            when (explore_location_search.visibility) {
                GONE -> {
                    explore_location_search.visibility = VISIBLE
                    explore_location_fab_search.backgroundTintList =
                        ContextCompat.getColorStateList(it.context, R.color.gray)
                }
                VISIBLE -> {
                    explore_location_search.visibility = GONE
                    explore_location_search_recycler_view.visibility = GONE
                    explore_location_fab_search.backgroundTintList =
                        ContextCompat.getColorStateList(it.context, R.color.main_blue)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLocationInfoUI() {
        locationInfoFold.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isTouchInfoFold = true
                }
                MotionEvent.ACTION_UP -> {
                    if (isTouchInfoFold) {
                        locationInfoContainer.animate().alpha(0f).translationY(0f).duration = 250
                        isTouchInfoFold = false
                        return@setOnTouchListener false
                    }
                }
            }
            true
        }

        locationInfoDescriptionAdd.setOnClickListener {
            hideLocationInfo()
            val intent = Intent(it.context, AddLocationNoteActivity::class.java)
            intent.putExtra("latLng", myLocation)
            intent.putExtra("address", myAddress)

            (it.context as MainActivity).startActivityForResult(
                intent,
                ADD_LOCATION_NOTE_REQUEST_CODE
            )
            (it.context as MainActivity).overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
        }

        locationInfoFold.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isTouchInfoFold = true
                }
                MotionEvent.ACTION_UP -> {
                    if (isTouchInfoFold) {
                        locationInfoContainer.animate().alpha(0f).translationY(0f).duration = 250
                        isTouchInfoFold = false
                        return@setOnTouchListener false
                    }
                }
            }
            true
        }
    }
}