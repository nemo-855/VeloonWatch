package com.nemo.veloon.util

import android.content.Context
import android.location.LocationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationUtils @Inject constructor(@ApplicationContext private val context: Context)  {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    fun isLocationProviderEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}