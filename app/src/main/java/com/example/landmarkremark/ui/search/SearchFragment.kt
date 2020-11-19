package com.example.landmarkremark.ui.search

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.landmarkremark.R
import com.example.landmarkremark.ui.main.MainActivity
import com.example.landmarkremark.ui.main.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import java.util.*
import androidx.lifecycle.Observer
import com.example.landmarkremark.models.LocationData
import com.example.landmarkremark.utilities.SharedPreferenceUtils
import com.google.android.gms.maps.model.*


class SearchFragment : Fragment(), LocationListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val EDIT_LOCATION_REQUEST_CODE = 2
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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as MainActivity

        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(context, Locale.getDefault())
        search_location_map.onCreate(savedInstanceState)
        mainViewModel.getLocations().observe(viewLifecycleOwner, Observer {
            Timber.d("getLocations - search")
            if(::map.isInitialized) {
                updateMapWithNotes(it)
            } else {
                mainViewModel.getLocations()
            }
        })
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
            } else {
                showPickerUI()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        search_location_map.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        search_location_map.onResume()

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
                } else {
                    showPickerUI()
                }
            } else {
                showMapUI()
            }
        }
    }

    private fun updateMapWithNotes(locations: List<LocationData>) {
        val accessToken = SharedPreferenceUtils.getAccessToken()
        Timber.d("checkPosition -->updateMapWithNotes: $accessToken - ${::map.isInitialized}")
        if(::map.isInitialized) {
            for (data: LocationData in locations) {
                if (data.lat != null && data.long != null) {
                    val markOptions = MarkerOptions()
                    if(data.creatorId == accessToken) {
                        Timber.d("checkPosition spot my note: ${data.title}")
                        markOptions.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN))
                    }
                    markOptions.title(data.title!!).position(LatLng(data.lat, data.long))
                    map.addMarker(markOptions)
                }
            }
        }
        Timber.d("checkPosition <--updateMapWithNotes")
    }

    private fun showPickerUI() {

    }

    private fun showMapUI() {
        val context = this.context ?: return

        TransitionManager.beginDelayedTransition(search_location_container)
        search_location_text.visibility = View.VISIBLE
        search_location_edit.visibility = View.VISIBLE
        search_location_map.visibility = View.VISIBLE

        search_location_picker.visibility = View.GONE

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        MapsInitializer.initialize(context)

        search_location_text.setOnClickListener {
            search_location_edit.callOnClick()
        }

//        search_location_edit.setOnClickListener {
//            val intent = Intent(context, SignUpLocationEditActivity::class.java)
//            intent.putExtra(ADDRESS, address)
//            intent.putExtra(COUNTRY, country)
//            startActivityForResult(intent, EDIT_LOCATION_REQUEST_CODE)
//        }

        search_location_map.getMapAsync {
            map = it
            map.setOnMapClickListener { latLng ->
                updateLocation(latLng)
            }
            map.setOnMapLongClickListener { latLng ->
                updateLocation(latLng)
            }
            map.setOnInfoWindowClickListener {
                search_location_edit.callOnClick()
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
                            updateLocation(LatLng(-34.0, 151.0))
                        }

                    } else {
                        Timber.d("lastLocation from fusedLocationClient: ${location?.latitude}, ${location?.longitude}")
                        updateLocation(LatLng(location.latitude, location.longitude))
                    }
                } catch (e: Exception) {
                    Timber.e(e)

                    showPickerUI()
                }
            }
        }
    }

    private fun updateLocation(latLng: LatLng, ignoreChangeText: Boolean = false) {
        if (::map.isInitialized) {
            marker?.remove()
            marker = map.addMarker(MarkerOptions().position(latLng).title("My Location"))
            marker?.isVisible = true
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(
                        latLng
                    ).zoom(10F).tilt(30F).build()
                )
            )
        }

        if (ignoreChangeText) return

        val addressFromLatLng = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (!addressFromLatLng.isNullOrEmpty()) {
            val address = addressFromLatLng[0]?.getAddressLine(0)
//            country = addressFromLatLng[0]?.countryName
            search_location_text?.text = address
        }
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
            showPickerUI()
        }
        dialog.show()
    }
}