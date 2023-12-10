package com.nemo.veloon.data.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nemo.veloon.data.foregroundservice.BikingActivityService

class BikingActivityServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val targetIntent = Intent(p0, BikingActivityService::class.java)
        p0?.stopService(targetIntent)
    }
}