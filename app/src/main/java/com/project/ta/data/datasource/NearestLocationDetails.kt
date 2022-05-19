package com.project.ta.data.datasource

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import com.project.ta.data.datasource.remote.GeometryDetails
import com.project.ta.data.datasource.remote.LocationPhoto

data class NearestLocationDetails(
    @field:SerializedName("geometry") val geometry: GeometryDetails,
    @field:SerializedName("name") val placeName: String,
    @field:SerializedName("icon") val icon: String,
    @field:SerializedName("photos") val photos: List<LocationPhoto>,
    var photoURL: String
)
