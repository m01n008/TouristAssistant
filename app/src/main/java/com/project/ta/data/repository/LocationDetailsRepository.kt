package com.project.ta.data.repository

import androidx.compose.material.ListItem
import com.google.android.gms.maps.model.LatLng
import com.project.ta.data.datasource.GoogleMapServiceResponse
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.datasource.remote.api.GoogleMapService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class LocationDetailsRepository @Inject constructor(
   private val googleMapService: GoogleMapService
) {

    suspend fun getNearestAttractionLocations(): Flow<List<NearestLocationDetails>> {

        val mapServiceResponse = googleMapService.getNearestLocations("-33.8670522,151.1957362",1500,"attractions","")
        return {mapServiceResponse.results}.asFlow()

    }
    suspend fun getNearestServiceLocations(): Flow<List<NearestLocationDetails>> {

        val mapServiceResponse = googleMapService.getNearestLocations("-33.8670522,151.1957362",1500,"services","")
        return {mapServiceResponse.results}.asFlow()

    }

}