package com.project.ta.data.datasource

import com.google.gson.annotations.SerializedName

data class GoogleMapServiceResponse(
    @field:SerializedName("results") val results: List<NearestLocationDetails>
    )
