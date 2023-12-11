package com.nemo.veloon.di

import android.content.Context
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.data.sensor.ActivitySensorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideActivitySensor(@ApplicationContext context: Context): ActivitySensor {
        return ActivitySensorImpl(context = context)
    }
}
