package com.project.ta.data.datasource.remote.api

import com.project.ta.BuildConfig
import com.project.ta.data.datasource.GoogleMapServiceResponse
import com.project.ta.data.datasource.remote.LocationPhoto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface GoogleMapService {
    @GET("nearbysearch/json")
    suspend fun getNearestLocations(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String = BuildConfig.MAP_API_KEY
    ): GoogleMapServiceResponse

    @GET("/photo")
    suspend fun getLocationPhoto(
        @Query("maxwidth") maxWidth: Int,
        @Query("photo_reference") photoReference: String,
        @Query("key") apiKey: String = BuildConfig.MAP_API_KEY
    ): ByteArray




    companion object {

        private const val BASE_URL: String  = "https://maps.googleapis.com/maps/api/place/";
        fun create(): GoogleMapService{
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder().addInterceptor(logger).build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleMapService::class.java)


        }




    }

}