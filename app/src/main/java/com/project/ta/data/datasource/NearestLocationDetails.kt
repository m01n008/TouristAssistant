package com.project.ta.data.datasource

import com.google.gson.annotations.SerializedName
import com.project.ta.data.datasource.remote.GeometryDetails

data class NearestLocationDetails(
    @field:SerializedName("geometry") val geometry: GeometryDetails,
    @field:SerializedName("name") val placeName: String,
    @field:SerializedName("icon") val icon: String
)
