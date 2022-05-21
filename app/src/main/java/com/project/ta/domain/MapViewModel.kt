package com.project.ta.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.repository.LocationDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.net.URL

import javax.inject.Inject
import kotlin.math.ln

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationDetailsRepository: LocationDetailsRepository
): ViewModel() {
    private  var currentAttractiveLocationsResult: Flow<List<NearestLocationDetails>>? = null
    private  var currentServicesLocationsResult: Flow<List<NearestLocationDetails>>? = null


    suspend fun updateCurrentAttractions(lat: Double,lng: Double): Flow<List<NearestLocationDetails>>{
        val latestResult = locationDetailsRepository.getNearestAttractionLocations(lat, lng)
        currentAttractiveLocationsResult = latestResult
        return latestResult

    }
    suspend fun updateCurrentServices(lat: Double,lng: Double): Flow<List<NearestLocationDetails>>{
        val latestResult = locationDetailsRepository.getNearestServiceLocations(lat,lng)
        currentServicesLocationsResult = latestResult
        return latestResult

    }
    suspend fun getPhoto(pf: String):String?{
        val latestResult = locationDetailsRepository.get200WidthPhoto(pf)
        return latestResult
    }



}