package com.project.ta.di

import android.content.Context
import com.project.ta.data.datasource.local.AppDatabase
import com.project.ta.data.datasource.local.AttractionsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context):AppDatabase{
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideAttractionDao(appDatabase: AppDatabase):AttractionsDao{
        return appDatabase.attractionsDao()
    }


}