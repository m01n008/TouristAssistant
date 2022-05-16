package com.project.ta.data.datasource.remote

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class GeometryDetails(
    @field:SerializedName("location") val locationCoordinates: LatLngDetails

)

