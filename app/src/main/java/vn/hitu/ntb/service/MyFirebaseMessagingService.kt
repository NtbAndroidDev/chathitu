package vn.hitu.ntb.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import timber.log.Timber
import vn.hitu.ntb.R
import vn.hitu.ntb.broadcast.DirectReplyReceiver
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import java.util.Objects

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val messagingStyle = NotificationCompat.MessagingStyle("ChatHITU")
        .setConversationTitle("ChatHITU")
    var typeNotification = 0
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.tag("lololololo").d("From: %s", Gson().toJson(remoteMessage.from))
        Timber.d("Notification data %s", Gson().toJson(remoteMessage.data["body"]))

        val title   =   remoteMessage.data["title"]
        val body    =   remoteMessage.data["body"]
        val avt     =   remoteMessage.data["avt"]
        val gId     =   remoteMessage.data["gId"]
        val type    =   remoteMessage.data["type"]

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(gId!!, title, body!!, avt!!, type!!)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendNotification(gId : String, messageTitle: String?, messageBody: String?, avt : String, type : String) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val channelId = "fcm_default_channel"
        //        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.messenger)


        val replyLabel = "Nhập tin nhắn..."


        //Khi bấm nút gửi
        val remoteInput = RemoteInput.Builder("KEY_TEXT_REPLY").setLabel(replyLabel).build()

        val resultIntent = Intent(this, DirectReplyReceiver::class.java)
        resultIntent.putExtra(AppConstants.GROUP_ID, gId)
        resultIntent.putExtra(AppConstants.NOTIFICATION_ID, typeNotification)

        @SuppressLint("UnspecifiedImmutableFlag")
        val resultPendingIntentAction: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(applicationContext, 10, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            }else {
            PendingIntent.getBroadcast(applicationContext, 10, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }


        val action: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_logo, "Trả lời", resultPendingIntentAction).addRemoteInput(remoteInput).build()


        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .addAction(action)
                .setSmallIcon(R.drawable.ic_logo)
                .setColor(Color.parseColor("#7269EF"))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
//                .setContentText("https://123-zo.vn/intro-hau.html")
                .setLargeIcon(AppUtils.getBitmap(avt, this))
                .setAutoCancel(true)
                .setSound(sound)
                .setGroup(gId)
//                .setStyle(messagingStyle)
                .setGroupSummary(true)
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
        notificationManager.notify(gId , 1, notificationBuilder.build())

    }
}