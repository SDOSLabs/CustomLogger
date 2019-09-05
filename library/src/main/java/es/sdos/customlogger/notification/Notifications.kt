package es.sdos.customlogger.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.support.v4.app.NotificationCompat
import es.sdos.customlogger.log.CustomLog
import es.sdos.customlogger.R

/**
 * Sacada de LeakCanary: https://github.com/square/leakcanary
 */
internal object Notifications {

    private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"

    val canShowNotification: Boolean
        get() = canShowBackgroundNotifications || CustomLog.applicationVisible

    private val canShowBackgroundNotifications = if (SDK_INT >= O) {
        !CustomLog.application.packageManager.isInstantApp
    } else true

    fun showNotification(context: Context,
                         contentTitle: CharSequence,
                         contentText: CharSequence,
                         pendingIntent: PendingIntent?,
                         notificationId: Int,
                         type: NotificationType
    ) {
        if (!canShowNotification) {
            return
        }
        val builder = NotificationCompat.Builder(context,
            PRIMARY_CHANNEL_ID
        )
            .setContentText(contentText)
            .setContentTitle(contentTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notification =
            buildNotification(
                context,
                builder,
                type
            )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun buildNotification(context: Context,
                          builder: NotificationCompat.Builder,
                          type: NotificationType
    ): Notification {
        builder.setSmallIcon(R.drawable.ic_notification)
            .setWhen(System.currentTimeMillis())

        if (SDK_INT >= O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notificationChannel: NotificationChannel? =
                notificationManager.getNotificationChannel(type.name)
            if (notificationChannel == null) {
                val channelName = context.getString(type.nameResId)
                notificationChannel =
                    NotificationChannel(type.name, channelName, type.importance)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            builder.setChannelId(type.name)
        }

        return builder.build()
    }
}