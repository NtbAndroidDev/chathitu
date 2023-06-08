package vn.hitu.ntb.ui.dialog



import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.hjq.permissions.Permission
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.R
import vn.hitu.ntb.aop.CheckNet
import vn.hitu.ntb.aop.Permissions
import vn.hitu.ntb.aop.SingleClick
import vn.hitu.ntb.other.AppConfig
import vn.hitu.ntb.ui.activity.HomeActivity
import java.io.File


/**
 * @Author: NGUYỄN KHÁNH DUY
 * @Date: 26/10/2022
 */
class UpdateDialog {

    class Builder(context: HomeActivity) : BaseDialog.Builder<Builder>(context!!) {

        private val nameView: TextView? by lazy { findViewById(R.id.tv_update_name) }
        private val detailsView: TextView? by lazy { findViewById(R.id.tv_update_details) }
        private val updateView: TextView? by lazy { findViewById(R.id.tv_update_update) }
        private val closeView: TextView? by lazy { findViewById(R.id.tv_update_close) }

        /** Apk 文件 */
        private var apkFile: File? = null



        /** 是否强制更新 */
        private var forceUpdate = false


        /** 当前是否下载完毕 */
        private var goMarket : Intent? = null

        init {
            setContentView(R.layout.update_dialog)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setCancelable(false)
            setOnClickListener(updateView, closeView)
            detailsView?.movementMethod = ScrollingMovementMethod()
        }

        /**
         * 设置版本名
         */
        fun setVersionName(name: CharSequence?): Builder = apply {
            nameView?.text = name
        }

        /**
         * 设置更新日志
         */
        fun setUpdateLog(text: CharSequence?): Builder = apply {
            detailsView?.text = text
            detailsView?.visibility = if (text == null) View.GONE else View.VISIBLE
        }

        /**
         * 设置强制更新
         */
        fun setForceUpdate(force: Boolean): Builder = apply {
            forceUpdate = force
            closeView?.visibility = if (force) View.GONE else View.VISIBLE
            setCancelable(!force)
        }

        /**
         * 设置下载 url
         */
        fun setDownloadUrl(url: String?): Builder = apply {
            val localUri = Uri.parse(url)
             goMarket = Intent(Intent.ACTION_VIEW, localUri)
        }


        @SingleClick
        override fun onClick(view: View) {
            if (view === closeView) {
                dismiss()
            } else if (view === updateView) {
                // 判断下载状态
                startActivity(goMarket!!)
                dismiss()
            }
        }

        /**
         * 下载 Apk
         */
        @CheckNet
        @Permissions(
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE,
            Permission.REQUEST_INSTALL_PACKAGES
        )
        private fun downloadApk() {
            // 设置对话框不能被取消
            setCancelable(false)
            val notificationManager = getSystemService(NotificationManager::class.java)
            val notificationId = getContext().applicationInfo.uid
            var channelId = ""
            // 适配 Android 8.0 通知渠道新特性
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    getString(R.string.update_notification_channel_id),
                    getString(R.string.update_notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.enableLights(false)
                channel.enableVibration(false)
                channel.vibrationPattern = longArrayOf(0)
                channel.setSound(null, null)
                notificationManager.createNotificationChannel(channel)
                channelId = channel.id
            }
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(getContext(), channelId)
                    // 设置通知时间
                    .setWhen(System.currentTimeMillis())
                    // 设置通知标题
                    .setContentTitle(getString(R.string.app_name))
                    // 设置通知小图标
                    // 设置通知静音
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    // 设置震动频率
                    .setVibrate(longArrayOf(0))
                    // 设置声音文件
                    .setSound(null)
                    // 设置通知的优先级
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // 创建要下载的文件对象
            apkFile = File(
                getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                getString(R.string.app_name) + "_v" + nameView?.text.toString() + ".apk"
            )

        }

        /**
         * 安装 Apk
         */
        @Permissions(Permission.REQUEST_INSTALL_PACKAGES)
        private fun installApk() {
            getContext().startActivity(getInstallIntent())
        }

        /**
         * 获取安装意图
         */
        private fun getInstallIntent(): Intent {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val uri: Uri?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(
                    getContext(),
                    AppConfig.getPackageName() + ".provider",
                    apkFile!!
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                uri = Uri.fromFile(apkFile)
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }
}
