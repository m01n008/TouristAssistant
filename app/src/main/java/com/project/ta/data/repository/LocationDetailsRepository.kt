package com.project.ta.data.repository

import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.datasource.remote.LocationPhoto
import com.project.ta.data.datasource.remote.api.GoogleMapService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.*
import javax.inject.Inject

class LocationDetailsRepository @Inject constructor(
   private val googleMapService: GoogleMapService

) {
    val locationPhotoList: MutableList<LocationPhoto> = arrayListOf()
    suspend fun getNearestAttractionLocations(lat: Double,lng: Double): Flow<List<NearestLocationDetails>> {

        val mapServiceResponse = googleMapService.getNearestLocations(lat.toString()+","+lng.toString(),2000,"attractions","")
        return {mapServiceResponse.results}.asFlow()

    }
    suspend fun getNearestServiceLocations(lat: Double,lng: Double): Flow<List<NearestLocationDetails>> {

        val mapServiceResponse = googleMapService.getNearestLocations(lat.toString()+","+lng.toString(),2000,"services","")
        return {mapServiceResponse.results}.asFlow()


    }
    suspend fun get200WidthPhoto(photoReference: String): String? {
        val mapServiceResponse = googleMapService.getLocationPhoto(200, photoReference)
        return mapServiceResponse.toString()
    }






}