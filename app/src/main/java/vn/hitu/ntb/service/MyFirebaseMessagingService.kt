package vn.hitu.ntb.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import timber.log.Timber
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import java.util.concurrent.ThreadLocalRandom

class MyFirebaseMessagingService : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.tag("lololololo").d("From: %s", Gson().toJson(remoteMessage.from))
        Timber.d("Notification data %s", Gson().toJson(remoteMessage.data["body"]))

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val avt = remoteMessage.data["avt"]

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(title, body!!, avt!!)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendNotification(messageTitle: String?, messageBody: String?, avt : String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "fcm_default_channel"
        //        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.messenger)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_logo)
                .setColor(Color.parseColor("#7269EF"))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setLargeIcon(AppUtils.getBitmap(avt, this))
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        notificationManager!!.notify(ThreadLocalRandom.current().nextInt(0, 1000), notificationBuilder.build())

    }
}