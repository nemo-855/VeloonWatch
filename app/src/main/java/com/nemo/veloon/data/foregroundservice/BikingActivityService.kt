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
import com.nemo.veloon.data.sensor.ActivitySensorImpl
import com.nemo.veloon.ui.MainActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BikingActivityService : Service() {
    companion object {
        const val REQUEST_TO_STOP_SERVICE = "RequestToStopService"
        private const val CHANNEL_ID = "BikingActivityServiceChannel"
        private const val CHANNEL_NAME = "BikingActivityService Channel"
        private const val FOREGROUND_SERVICE_ID = 81219
    }

    private lateinit var bikingActivityRepository: BikingActivityRepository

    private var collectIsBikingJob: Job? = null

    private val defaultCoroutineScope = CoroutineScope(CoroutineExceptionHandler { _, _ ->
        // no-op
    })

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext
        bikingActivityRepository = BikingActivityRepository(
            bikingActivityDataStore = BikingActivityDataStore(context),
            activitySensor = ActivitySensorImpl(context),
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(REQUEST_TO_STOP_SERVICE)) {
            onRequestedToStopService()
        } else {
            startForeground(FOREGROUND_SERVICE_ID, createNotification(applicationContext))
            onStartService()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyService()
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
        val handlingExceptionCoroutineScope = CoroutineScope(
            CoroutineExceptionHandler { _, _ ->
                defaultCoroutineScope.launch { bikingActivityRepository.finishBiking() }
                stopSelf()
            }
        )

        collectIsBikingJob = handlingExceptionCoroutineScope.launch {
            bikingActivityRepository.isBikingFlow.collect { isBiking ->
                if (!isBiking) stopSelf()
            }
        }

        handlingExceptionCoroutineScope.launch {
            bikingActivityRepository.startBiking()
            collectIsBikingJob?.start()
        }
    }

    private fun onRequestedToStopService() {
        defaultCoroutineScope.launch {
            bikingActivityRepository.finishBiking()
        }
    }

    private fun onDestroyService() {
        collectIsBikingJob?.cancel()
        collectIsBikingJob = null
    }
}