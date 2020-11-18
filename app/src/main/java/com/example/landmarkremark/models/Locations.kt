package com.example.landmarkremark.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class Locations (
    @SerializedName("id")
    val id: Boolean?
): Parcelable