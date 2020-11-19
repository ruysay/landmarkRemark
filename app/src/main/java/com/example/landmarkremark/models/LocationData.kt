package com.example.landmarkremark.models

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
@Parcelize
data class LocationData (
    val id: String? = "",
    val title: String? = "",
    val description: String? = "",
    val createdTime: String? = "",
    val creatorId: String? = "",
    val lat: Double? = 0.0,
    val long: Double? = 0.0,
    val extra: String? = "",
    val visibility: String? = "",
    val imageUrl: String? = ""
): Parcelable