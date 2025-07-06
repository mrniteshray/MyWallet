package xcom.appbrahma.xapps.MyWallet.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService

object NotificationUtils {

    fun createNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = "BudgetLimit"
            val ChannelName = "Budget"
            val description = "Show Budget limit exceed notification"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId,ChannelName,importance).apply {
                this.description = description
            }

            val notificationmanager : NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationmanager.createNotificationChannel(channel)

        }
    }
}