package vn.hitu.ntb.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import timber.log.Timber
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.app.AppApplication.Companion.SOCIAL_NOTIFICATION_ID
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import java.util.concurrent.ExecutionException
import java.util.concurrent.ThreadLocalRandom


class NotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val mmkv: MMKV = MMKV.mmkvWithID("push_token")
        mmkv.putString(AppConstants.PUSH_TOKEN, token).commit()
        Timber.d("push_token_Data %s", token)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("Notification data %s", Gson().toJson(message.data))
        Timber.d("Notification bundle %s", Gson().toJson(message))

        val title = message.data.get("title").toString()
        val fullName = message.data.get("title").toString()
        val avatar = message.data.get("avatar").toString()
        val body = message.data.get("content").toString()
        val uuid = message.data.get("uuid").toString()
        sendNotification(
            title,
            fullName,
            avatar,
            body,
            message.data["object_id"].toString(),
            message.data["object_type"].toString(),
            message.data["uuid"].toString()
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("CheckResult", "WrongConstant")
    @Throws(
        ExecutionException::class, InterruptedException::class
    )


    private fun sendNotification(
        title: String,
        fullName: String,
        avatar: String,
        body: String,
        value: String,
        type: String,
        uuid: String
    ) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(AppConstants.OBJECT_ID, value)
        intent.putExtra(AppConstants.OBJECT_TYPE, type)
        intent.putExtra(AppConstants.UUID, uuid)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(HomeActivity::class.java)
        stackBuilder.addNextIntent(intent)

        val resultPendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                stackBuilder.getPendingIntent(
                    0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val notification: Notification = NotificationCompat.Builder(this, SOCIAL_NOTIFICATION_ID)
            .setSmallIcon(vn.hitu.ntb.R.drawable.ic_logo_notification)
            .setColor(Color.parseColor("#FFA233"))
            .setShortcutId(avatar)
            .setContentTitle(title)
            .setLargeIcon(AppUtils.getBitmap(avatar, this))
            .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT))).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).setContentIntent(resultPendingIntent).setAutoCancel(true).setOnlyAlertOnce(false)
            .setDefaults(Notification.DEFAULT_ALL).setShowWhen(true).build()

        AppApplication.applicationContext().getNotificationManager()!!
            .notify(ThreadLocalRandom.current().nextInt(0, 1000), notification)
    }
}
