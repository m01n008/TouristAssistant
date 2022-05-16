package com.project.ta.domain

import androidx.lifecycle.ViewModel
import com.project.ta.data.datasource.NearestLocationDetails
import com.project.ta.data.repository.LocationDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationDetailsRepository: LocationDetailsRepository
): ViewModel() {
    private  var currentAttractiveLocationsResult: Flow<List<NearestLocationDetails>>? = null
    private  var currentServicesLocationsResult: Flow<List<NearestLocationDetails>>? = null


    suspend fun updateCurrentAttractions(): Flow<List<NearestLocationDetails>>{
        val latestResult = locationDetailsRepository.getNearestAttractionLocations()
        currentAttractiveLocationsResult = latestResult
        return latestResult

    }
    suspend fun updateCurrentServices(): Flow<List<NearestLocationDetails>>{
        val latestResult = locationDetailsRepository.getNearestServiceLocations()
        currentServicesLocationsResult = latestResult
        return latestResult

    }


}