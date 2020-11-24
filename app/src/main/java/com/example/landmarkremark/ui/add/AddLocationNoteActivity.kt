package com.example.landmarkremark.ui.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.landmarkremark.R
import com.example.landmarkremark.models.LocationData
import com.example.landmarkremark.ui.main.MainRepository
import com.example.landmarkremark.widgets.LoadingDialog
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_loaction_note.*

class AddLocationNoteActivity : AppCompatActivity() {

    private lateinit var loadingDialog: LoadingDialog
    // LatLng of the new location note
    private var latLng: LatLng? = null
    // address of selected location
    private var address: String? = null
    //Newly added LocationData
    private var locationData: LocationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loaction_note)

        loadingDialog = LoadingDialog(this)
        val intent = intent
        latLng = intent.getParcelableExtra<Parcelable>("latLng") as LatLng?
        address = intent.getStringExtra("address")

        add_location_note_back.setOnClickListener {
            onBackPressed()
        }

        add_location_note_address_text.text = address

        add_location_note_next.setOnClickListener {
            locationData = MainRepository.writeNote(
                add_location_note_info_title.text.toString(),
                add_location_note_info_description.text.toString(),
                latLng?.latitude,
                latLng?.longitude,
                extra = "",
                visibility = if (add_location_note_info_visibility_checkbox.isChecked) "public" else "private"
            )
            MainRepository.getMyLocations()

            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.putExtra("locationData", locationData)
        setResult(Activity.RESULT_OK, intent)
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
    }
}