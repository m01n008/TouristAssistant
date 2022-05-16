package com.project.ta.data.repository

import com.project.ta.data.datasource.local.Attractions
import com.project.ta.data.datasource.local.AttractionsDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttractionsRepository @Inject constructor(
    val attractionsDao: AttractionsDao
) {
    suspend fun insertAttraction(attraction: Attractions){
        attractionsDao.insert(attraction)
    }
    suspend fun insertAllAttractions(attractions: List<Attractions>){
        attractionsDao.insertAll(attractions)
    }

    fun getAttractions(): Flow<List<Attractions>> {
       return attractionsDao.getAttractions()
    }

    fun getAttractionsByType(attractionType: String) =
        attractionsDao.getAttractionsByType(attractionType)



}