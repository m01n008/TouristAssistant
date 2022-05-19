package com.project.ta.data.repository

import android.graphics.Bitmap
import androidx.compose.material.ListItem
import com.google.android.gms.maps.model.LatLng
import com.project.ta.data.datasource.GoogleMapServiceResponse
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.datasource.remote.LocationPhoto
import com.project.ta.data.datasource.remote.api.GoogleMapService
import com.project.ta.util.filetoBitmap
import com.project.ta.util.toBitmap
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.*
import javax.inject.Inject

class LocationDetailsRepository @Inject constructor(
   private val googleMapService: GoogleMapService

) {
    val locationPhotoList: MutableList<LocationPhoto> = arrayListOf()
    suspend fun getNearestAttractionLocations(): Flow<List<NearestLocationDetails>> {

        val mapServiceResponse = googleMapService.getNearestLocations("-33.8670522,151.1957362",1500,"attractions","")
        return {mapServiceResponse.results}.asFlow()

    }
    suspend fun getNearestServiceLocations(): Flow<List<NearestLocationDetails>> {

        val mapServiceResponse = googleMapService.getNearestLocations("-33.8670522,151.1957362",1500,"services","")
        return {mapServiceResponse.results}.asFlow()


    }
    suspend fun get200WidthPhoto(photoReference: String): String? {
        val mapServiceResponse = googleMapService.getLocationPhoto(200, photoReference)
        return mapServiceResponse.toString()
    }






}