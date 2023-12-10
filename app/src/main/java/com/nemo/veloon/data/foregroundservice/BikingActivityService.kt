package com.nemo.veloon.data.foregroundservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nemo.veloon.data.broadcastreceiver.BikingActivityServiceBroadcastReceiver
import com.nemo.veloon.data.datastore.BikingActivityDataStore
import com.nemo.veloon.data.repository.BikingActivityRepository
import com.nemo.veloon.data.sensor.ActivitySensor
import com.nemo.veloon.data.sensor.ActivitySensorImpl
import com.nemo.veloon.ui.MainActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class BikingActivityService : Service() {
    companion object {
        private const val CHANNEL_ID = "BikingActivityServiceChannel"
        private const val CHANNEL_NAME = "BikingActivityService Channel"
        private const val FOREGROUND_SERVICE_ID = 81219
    }

    private lateinit var bikingActivityRepository: BikingActivityRepository
    private lateinit var activitySensor: ActivitySensor

    private val activitySensorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable !is ActivitySensor.ActivitySensorException) return@CoroutineExceptionHandler
        when (throwable) {
            ActivitySensor.ActivitySensorException.AnotherExerciseIsInProgress -> {
                // TODO Implement
            }

            ActivitySensor.ActivitySensorException.BikingMeasurementIsNotSupported -> {
                // TODO Implement
            }
        }
        stopService(Intent(this, BikingActivityService::class.java))
    }

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
        CoroutineScope(activitySensorExceptionHandler).launch {
            bikingActivityRepository.setIsBiking(true)
            activitySensor.start()
            activitySensor.current.collect {
                bikingActivityRepository.setBikingPace(it.activity.pace)
                bikingActivityRepository.setBikingDistance(it.activity.distance)
            }
        }

        //1．通知領域タップで戻ってくる先のActivity
        val openIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        //2．通知チャネル登録
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        //3．ブロードキャストレシーバーをPendingIntent化
        val sendIntent = Intent(this, BikingActivityServiceBroadcastReceiver::class.java).apply {
            action = Intent.ACTION_SEND
        }
        val sendPendingIntent = PendingIntent.getBroadcast(this, 0, sendIntent, PendingIntent.FLAG_IMMUTABLE)

        //4．通知の作成（ここでPendingIntentを通知領域に渡す）
        val notification = NotificationCompat.Builder(this, CHANNEL_ID )
            .setContentTitle("フォアグラウンドのテスト中")
            .setContentText("終了する場合はこちらから行って下さい。")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openIntent)
            .addAction(androidx.core.R.drawable.notification_action_background, "実行終了", sendPendingIntent)
            .build()

        //5．フォアグラウンド開始。
        startForeground(FOREGROUND_SERVICE_ID, notification)

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        CoroutineScope(activitySensorExceptionHandler).launch {
            bikingActivityRepository.setIsBiking(false)
            activitySensor.stop()
        }
        stopSelf()
        return super.stopService(name)
    }
}