package com.project.ta.data.datasource.remote

import com.google.gson.annotations.SerializedName

data class LatLngDetails(
    @field:SerializedName("lat") val lat: Double,
    @field:SerializedName("lng") val lng: Double,

    )
