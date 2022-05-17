package com.project.ta.di

import com.project.ta.data.datasource.remote.LocationPhoto
import com.project.ta.data.datasource.remote.api.GoogleMapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGoogleMapService():GoogleMapService{
        return GoogleMapService.create()

    }

}