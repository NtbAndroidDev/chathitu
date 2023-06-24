package vn.hitu.ntb.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.utils.AppUtils


class DirectReplyReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val extras = intent.extras
        if (remoteInput != null) {
            val groupID: String = extras!!.getString(AppConstants.GROUP_ID)!!
            val replyText = remoteInput.getCharSequence("KEY_TEXT_REPLY")
            val notifyId = extras.getInt(AppConstants.NOTIFICATION_ID)


            AppUtils.sendMessage(replyText.toString(), groupID, AppConstants.TEXT)

        }
    }
}

