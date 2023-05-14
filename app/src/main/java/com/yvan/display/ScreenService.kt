package com.yvan.display
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.yvan.display.R

/**
 * 开启 前台服务
 */
class ScreenService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "com.yvan.mutidisplay.channel_id"
    private val NOTIFICATION_CHANNEL_NAME = "com.yvan.mutidisplay.channel_name"
    private val NOTIFICATION_CHANNEL_DESC = "com.yvan.mutidisplay.channel_desc"

    // 开启 前台服务 的 通知【正在录屏中，其实就是，正在屏幕共享中，双屏开启中，等，此含义】
    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationIntent = Intent(this, ScreenService::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon( BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground)) // 录屏通知的答图标等信息
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 录屏通知的小图标等信息
                .setContentTitle("录屏服务")
                .setContentText("正在录屏......")
                .setContentIntent(pendingIntent)
                .build() // 最后构建通知的所有信息 即可
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = NOTIFICATION_CHANNEL_DESC
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            startForeground(1, notification) // 开启通知 此 录屏服务 的 前台通知 正在录屏中...
        }
    }

    override fun onCreate() {
        super.onCreate()
        startNotification() // 此后台服务一旦启动，就需要马上显示 前台通知，告知用户，正在录屏中...
    }

    override fun onBind(intent: Intent): IBinder? = null
}