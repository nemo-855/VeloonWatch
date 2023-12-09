package com.nemo.veloon.di.data

import android.content.Context
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.data.sensor.ActivitySensorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object DataModule {
    @Provides
    fun provideActivitySensor(@ApplicationContext context: Context): ActivitySensor {
        return ActivitySensorImpl(context = context)
    }
}
