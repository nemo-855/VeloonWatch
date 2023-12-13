package com.nemo.veloon.data.foregroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nemo.veloon.R
import com.nemo.veloon.data.broadcastreceiver.BikingActivityServiceBroadcastReceiver
import com.nemo.veloon.data.datastore.BikingActivityDataStore
import com.nemo.veloon.data.repository.BikingActivityRepository
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.data.sensor.ActivitySensorImpl
import com.nemo.veloon.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BikingActivityService : Service() {
    companion object {
        private const val CHANNEL_ID = "BikingActivityServiceChannel"
        private const val CHANNEL_NAME = "BikingActivityService Channel"
        private const val FOREGROUND_SERVICE_ID = 81219
    }

    private lateinit var bikingActivityRepository: BikingActivityRepository
    private lateinit var activitySensor: ActivitySensor

    private val job = SupervisorJob()
    private val foregroundServiceScope =
        CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext
        bikingActivityRepository = BikingActivityRepository(BikingActivityDataStore(context))
        activitySensor = ActivitySensorImpl(context)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_SERVICE_ID, createNotification(applicationContext))
        onStartService()
        return START_STICKY
    }

    override fun onDestroy() {
        runBlocking {
            try {
                finishBiking()
            } catch (e: Exception) {
                // no-op
            }
        }
        job.cancel()
        super.onDestroy()
    }

    private fun createNotification(context: Context): Notification {
        //1．通知領域タップで戻ってくる先のActivity
        val openIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        //2．通知チャネル登録
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        //3．ブロードキャストレシーバーをPendingIntent化
        val sendIntent = Intent(this, BikingActivityServiceBroadcastReceiver::class.java).apply {
            action = Intent.ACTION_SEND
        }
        val sendPendingIntent =
            PendingIntent.getBroadcast(this, 0, sendIntent, PendingIntent.FLAG_IMMUTABLE)

        //4．通知の作成（ここでPendingIntentを通知領域に渡す）
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.home_panel_notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openIntent)
            .addAction(
                R.drawable.stop_outlined,
                context.getString(R.string.home_panel_notification_stop_text),
                sendPendingIntent
            )
            .build()
    }

    private fun onStartService() {
        foregroundServiceScope.launch {
            runCatching {
                bikingActivityRepository.startBiking()
                activitySensor.start()
                activitySensor.current.collect {
                    bikingActivityRepository.setActivityState(it)
                }
            }.onSuccess {
                // no-op
            }.onFailure {
                finishBiking()
                stopSelf()
            }
        }
    }

    private suspend fun finishBiking() {
        bikingActivityRepository.finishBiking()
        activitySensor.stop()
    }
}