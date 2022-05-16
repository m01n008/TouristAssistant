package com.project.ta.data.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
data class Attractions (
    @PrimaryKey @ColumnInfo(name = "place_id") val placeId: Int,
    val name: String,
    val attractionType: String,
    val descripton: String,
    val imageURl: String? = "",
    val iconURL: String? = ""
        ){



}
