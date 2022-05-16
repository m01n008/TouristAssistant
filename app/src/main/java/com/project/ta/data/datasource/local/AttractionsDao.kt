package com.project.ta.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionsDao {

    @Query("SELECT * from attractions order by name")
    fun getAttractions():Flow<List<Attractions>>

    @Query("SELECT * from attractions where attractionType = :attractionType")
    fun getAttractionsByType(attractionType:String): Flow<List<Attractions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attractions: List<Attractions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attractions: Attractions)

}