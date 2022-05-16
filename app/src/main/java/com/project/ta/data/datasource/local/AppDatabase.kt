package com.project.ta.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.ta.util.DATABASE_NAME

@Database(entities = [Attractions::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase :RoomDatabase() {

    abstract fun attractionsDao(): AttractionsDao

    companion object{

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context):AppDatabase{
            return Room.databaseBuilder(context,AppDatabase::class.java, DATABASE_NAME).build()
        }
    }




}