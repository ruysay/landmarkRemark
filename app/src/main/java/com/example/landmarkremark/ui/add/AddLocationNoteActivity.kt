package com.example.landmarkremark.ui.add

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.ui.main.MainRepository
import com.example.landmarkremark.widgets.LoadingDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_add_loaction_note.*
import timber.log.Timber
import java.util.*


class AddLocationNoteActivity() : AppCompatActivity() {

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loaction_note)

        geocoder = Geocoder(this, Locale.getDefault())
        loadingDialog = LoadingDialog(this)


        val intent = intent
        myLocation = intent.getParcelableExtra<Parcelable>("latLng") as LatLng?
        Timber.d("checkLocation: ${myLocation?.latitude}")

        add_location_note_back.setOnClickListener {
            onBackPressed()
        }
        add_location_note_next.setOnClickListener {
            MainRepository.writeNote(add_location_note_info_title.text.toString(),
                add_location_note_info_description.text.toString(),
                myLocation?.latitude,
                myLocation?.longitude)
            onBackPressed()
        }
    }

    private var myLocation: LatLng? = null

    private fun updateLocation(latLng: LatLng, ignoreChangeText: Boolean = false) {
        if (::map.isInitialized) {
            marker?.remove()
            marker =
                map.addMarker(MarkerOptions().position(latLng).title(getString(R.string.title_my_location)))
            marker?.isVisible = true
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(
                        latLng
                    ).zoom(10F).tilt(30F).build()
                )
            )
        }

        myLocation = latLng

        if (ignoreChangeText) return

        val addressFromLatLng = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (!addressFromLatLng.isNullOrEmpty()) {
            val address = addressFromLatLng[0]?.getAddressLine(0)

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
    }
}