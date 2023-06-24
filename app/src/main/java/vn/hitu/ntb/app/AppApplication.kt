package vn.hitu.ntb.app

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioAttributes
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.Display
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.aghajari.emojiview.AXEmojiManager
import com.aghajari.emojiview.facebookprovider.AXFacebookEmojiProvider
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.hjq.bar.TitleBar
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestInterceptor
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.http.request.HttpRequest
import com.hjq.toast.ToastUtils
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.app.PictureAppMaster
import com.luck.picture.lib.engine.PictureSelectorEngine
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.tencent.mmkv.MMKV
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.PollingXHR
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.extensions.flagsCompat
import net.gotev.uploadservice.extensions.getCancelUploadIntent
import net.gotev.uploadservice.observer.request.GlobalRequestObserver
import okhttp3.OkHttpClient
import timber.log.Timber
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.R
import vn.hitu.ntb.aop.Log
import vn.hitu.ntb.cache.ConfigCache
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.database.*
import vn.hitu.ntb.manager.ActivityManager
import vn.hitu.ntb.other.*
import vn.hitu.ntb.other.sticker.UI
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Collections.singletonMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * @Author: Bùi Hửu Thắng
 * @Date: 28/09/2022
 */
class AppApplication : Application(), IApp {

    private var notificationManager: NotificationManager? = null
    private var sharedPreferences: SharedPreferences? = null

    @Log("Thời gian khởi động")
    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        initSdk(this)
        PictureAppMaster.getInstance().app = this
        FirebaseApp.initializeApp(this)
        if (sharedPreferences!!.getBoolean("night", false))
            setTheme(true)
        else
            setTheme(false)

        instance = this


        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        FirebaseApp.getInstance().setAutomaticResourceManagementEnabled(true)
        //Set up thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Tiến trình tải
            uploadChannel = NotificationChannel(
                UPLOAD_CHANNEL_ID, "Kênh tiến trình tải", NotificationManager.IMPORTANCE_LOW
            )
            uploadChannel.description =
                "Kênh thông báo tiến trình tải hình ảnh, video lên ứng dụng khách hàng Alo Line."
            uploadChannel.setSound(null, null)

            //Kênh thông báo khách hàng
            customerNotificationChannel = NotificationChannel(
                CUSTOMER_NOTIFICATION_CHANNEL_ID,
                "Kênh thông báo khách hàng",
                NotificationManager.IMPORTANCE_HIGH
            )
            customerNotificationChannel.description =
                "Nhận các thông báo đặt bàn, đơn hàng và các chương trình của nhà hàng."
            customerNotificationChannel.enableLights(true)
            customerNotificationChannel.enableVibration(true)
            customerNotificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val alarmSoundCustomer = Uri.parse(
                ("android.resource://" + AppConfig.getPackageName()) + "/" + R.raw.customer_notification_sound
            )
            val audioAttributesCustomer =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()
            customerNotificationChannel.setSound(
                alarmSoundCustomer, audioAttributesCustomer
            )

            //Kênh thông báo mạng
            socialNotificationChannel = NotificationChannel(
                SOCIAL_NOTIFICATION_ID,
                "Kênh thông báo mạng xã hội",
                NotificationManager.IMPORTANCE_HIGH
            )
            socialNotificationChannel.description =
                "Nhận các thông báo bài đăng, bình luận, bạn bè."
            socialNotificationChannel.enableLights(true)
            socialNotificationChannel.enableVibration(true)
            socialNotificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val alarmSoundSocial = Uri.parse(
                ("android.resource://" + AppConfig.getPackageName()) + "/" + R.raw.ring_ring
            )
            val audioAttributesSocial =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()
            socialNotificationChannel.setSound(
                alarmSoundSocial, audioAttributesSocial
            )

            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(uploadChannel)
            notificationManager!!.createNotificationChannel(customerNotificationChannel)
            notificationManager!!.createNotificationChannel(socialNotificationChannel)
        }

        val defaultDisplay =
            DisplayManagerCompat.getInstance(this).getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = createDisplayContext(defaultDisplay!!)

        widthDevice = displayContext.resources.displayMetrics.widthPixels
        heightDevice = displayContext.resources.displayMetrics.heightPixels
    }

    companion object {
        /**
         *
        Khởi tạo một số khuôn khổ của bên thứ ba
         */

        fun applicationContext(): AppApplication {
            return instance as AppApplication
        }

        @SuppressLint("HardwareIds")
        fun initSdk(application: Application) {
            // Đặt trình khởi tạo thanh tiêu đề
            TitleBar.setDefaultStyle(TitleBarStyle())

            FirebaseApp.initializeApp(application)

            // Đặt trình tạo Tiêu đề chung
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { _: Context?, _: RefreshLayout? ->
                MaterialHeader(
                    application
                ).setColorSchemeColors(
                    getColor(
                        application, R.color.common_accent_color
                    )
                )
            }

            SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
                layout.setEnableAutoLoadMore(false)
                layout.setEnableOverScrollDrag(true)
                layout.setEnableOverScrollBounce(true)
                layout.setEnableLoadMoreWhenContentNotFull(true)
                layout.setEnableScrollContentWhenRefreshed(true)
                layout.setPrimaryColorsId(R.color.main_bg, android.R.color.white)
                layout.setFooterMaxDragRate(4.0f)
                layout.setFooterHeight(0f)
            }

            // Khởi tạo toast
            ToastUtils.init(application, ToastStyle())

            //đặt chế độ gỡ lỗi
            ToastUtils.setDebugMode(AppConfig.isDebug())

            // cài đặt Toast
            ToastUtils.setInterceptor(ToastLogInterceptor())

            // cài đặt Crash
            CrashHandler.register(application)

            //Lưu cache
            MMKV.initialize(application)

            // Activity
            ActivityManager.getInstance().init(application)

            // Khởi tạo khung yêu cầu mạng
            val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

            //Khởi tạo máy chủ java
            EasyConfig.with(okHttpClient)
                // có in nhật ký không
                .setLogEnabled(AppConfig.isLogEnable())
                //Thiết lập cấu hình máy chủ
                .setServer(vn.hitu.ntb.http.model.RequestServer())
                //Đặt chính sách xử lý yêu cầu
                .setHandler(vn.hitu.ntb.http.model.RequestHandler(application))
                // Đặt bộ chặn tham số yêu cầu
                .setInterceptor(object : IRequestInterceptor {
                    override fun interceptArguments(
                        httpRequest: HttpRequest<*>, params: HttpParams, headers: HttpHeaders
                    ) {
                        headers.put("time", System.currentTimeMillis().toString())
                        headers.put("Content-Type", "application/json")
                        headers.put("Cache-Control", "no-store")
                        headers.put("Cache-Control", "no-cache")
                        headers.put(
                            "udid", Settings.Secure.getString(
                                application.contentResolver, Settings.Secure.ANDROID_ID
                            )
                        )
                        headers.put("os_name", "android")
                    }
                })
                // Đặt số lần yêu cầu thử lại
                .setRetryCount(4)
                // Đặt thời gian thử lại yêu cầu
                .setRetryTime(3000).into()

            // Khởi tạo in nhật ký
            if (AppConfig.isLogEnable()) {
                Timber.plant(DebugLoggerTree())
            }

            // Đăng ký theo dõi thay đổi trạng thái mạng
            val connectivityManager: ConnectivityManager? =
                ContextCompat.getSystemService(application, ConnectivityManager::class.java)
            if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(object :
                    ConnectivityManager.NetworkCallback() {
                    @SuppressLint("MissingPermission")
                    override fun onLost(network: Network) {
                        val topActivity: Activity? = ActivityManager.getInstance().getTopActivity()
                        if (topActivity !is LifecycleOwner) {
                            return
                        }
                        val lifecycleOwner: LifecycleOwner = topActivity
                        if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
                            return
                        }
                        ToastUtils.show(R.string.common_network_error)
                    }
                })
            }

            //Tải module
            val splitInstallManager = SplitInstallManagerFactory.create(application)
            val request = SplitInstallRequest.newBuilder().addModule("login").build()

            splitInstallManager.startInstall(request).addOnSuccessListener {
                //Sau khi tải xong
            }.addOnFailureListener {
                //Tải thất bại
            }

            //Bỏ qua bảo mật khi domain không có ssl
            sslHostnameSocket()



            //Cài đặt upload service
            UploadServiceConfig.initialize(
                context = application,
                defaultNotificationChannel = UPLOAD_CHANNEL_ID,
                debug = BuildConfig.DEBUG
            )

            GlobalRequestObserver(
                application, GlobalUploadObserver()
            )

            //Setup database
            appDatabase = AppDatabase.getDatabase(application, CoroutineScope(SupervisorJob()))
//            newsFeedPostDao = appDatabase?.newsFeedPostDao()
            contactDao = appDatabase?.contactDao()
            contactDeviceDao = appDatabase?.contactDeviceDao()
            stickerDao = appDatabase?.stickerDao()

            //Setup Emoji
            AXEmojiManager.install(application, AXFacebookEmojiProvider(application))
            UI.mEmojiView = true
            UI.mSingleEmojiView = false
            UI.mStickerView = true
            UI.mCustomView = false
            UI.mFooterView = true
            UI.mCustomFooter = false
            UI.mWhiteCategory = true
            UI.loadTheme()

            AXEmojiManager.getEmojiViewTheme().footerSelectedItemColor =
                getColor(application, R.color.main_bg)
            AXEmojiManager.getEmojiViewTheme().footerBackgroundColor = Color.WHITE
            AXEmojiManager.getEmojiViewTheme().selectionColor = Color.TRANSPARENT
            AXEmojiManager.getEmojiViewTheme().selectedColor =
                getColor(application, R.color.main_bg)
            AXEmojiManager.getEmojiViewTheme().categoryColor = Color.WHITE
            AXEmojiManager.getEmojiViewTheme().setAlwaysShowDivider(true)
            AXEmojiManager.getEmojiViewTheme().backgroundColor =
                getColor(application, R.color.white)

            AXEmojiManager.getStickerViewTheme().selectionColor =
                getColor(application, R.color.main_bg)
            AXEmojiManager.getStickerViewTheme().selectedColor =
                getColor(application, R.color.main_bg)
            AXEmojiManager.getStickerViewTheme().backgroundColor = Color.WHITE
            AXEmojiManager.getStickerViewTheme().categoryColor =
                getColor(application, R.color.white)
            AXEmojiManager.getStickerViewTheme().setAlwaysShowDivider(true)
            UI.setCategoryStickers(
                ArrayList()
            )
        }

        // Biến mặc định để lưu cache
        lateinit var kv: MMKV

        var socketChat: Socket? = null

        var socketOrder: Socket? = null

        var socketLogin: Socket? = null

        var appDatabase: AppDatabase? = null

//        var newsFeedPostDao: NewsFeedPostDao? = null

        var contactDeviceDao: ContactDeviceDao? = null

        var contactDao: ContactDao? = null

        var stickerDao: StickerDao? = null

        var widthDevice: Int = 0
        var heightDevice: Int = 0





        private fun sslHostnameSocket() {
            val trustAllCerts: Array<TrustManager> =
                arrayOf(@SuppressLint("CustomX509TrustManager") object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?, authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?, authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder.connectTimeout(20, TimeUnit.SECONDS)
            builder.writeTimeout(1, TimeUnit.MINUTES)
            builder.readTimeout(1, TimeUnit.MINUTES)
            IO.setDefaultOkHttpCallFactory(builder.build())
            IO.setDefaultOkHttpWebSocketFactory(builder.build())
        }

        fun getNotificationConfig(
            context: Context, uploadId: String, @StringRes title: Int
        ): UploadNotificationConfig {
            val clickIntent = PendingIntent.getActivity(
                context,
                1,
                Intent(context, HomeActivity::class.java),
                flagsCompat(PendingIntent.FLAG_UPDATE_CURRENT)
            )

            val autoClear = true
            val largeIcon: Bitmap? = null
            val clearOnAction = true
            val ringToneEnabled = true

            val cancelAction = UploadNotificationAction(
                R.drawable.ic_ban_notification,
                context.getString(R.string.cancel_upload),
                context.getCancelUploadIntent(uploadId)
            )

            val noActions = ArrayList<UploadNotificationAction>(1)
            val progressActions = ArrayList<UploadNotificationAction>(1)
            progressActions.add(cancelAction)

            val progress = UploadNotificationStatusConfig(
                context.getString(title),
                context.getString(R.string.uploading),
                R.drawable.ic_upload_notification,
                Color.BLUE,
                largeIcon,
                clickIntent,
                progressActions,
                clearOnAction,
                autoClear
            )

            val success = UploadNotificationStatusConfig(
                context.getString(title),
                context.getString(R.string.upload_success),
                R.drawable.ic_success_notification,
                Color.GREEN,
                largeIcon,
                clickIntent,
                noActions,
                clearOnAction,
                autoClear
            )

            val error = UploadNotificationStatusConfig(
                context.getString(title),
                context.getString(R.string.upload_error),
                R.drawable.ic_error_notification,
                Color.RED,
                largeIcon,
                clickIntent,
                noActions,
                clearOnAction,
                autoClear
            )

            val cancelled = UploadNotificationStatusConfig(
                context.getString(title),
                context.getString(R.string.upload_cancelled),
                R.drawable.ic_ban_notification,
                Color.YELLOW,
                largeIcon,
                clickIntent,
                noActions,
                clearOnAction
            )

            return UploadNotificationConfig(
                UPLOAD_CHANNEL_ID, ringToneEnabled, progress, success, error, cancelled
            )
        }

        var instance: AppApplication? = null

        const val UPLOAD_CHANNEL_ID = "UPLOAD_CHANNEL_ID" // Kênh thông báo upload
        lateinit var uploadChannel: NotificationChannel

        const val CUSTOMER_NOTIFICATION_CHANNEL_ID = "CUSTOMER_NOTIFICATION_CHANNEL_ID"
        lateinit var customerNotificationChannel: NotificationChannel // Kênh thông báo hoạt động khách khách hàng

        const val SOCIAL_NOTIFICATION_ID = "SOCIAL_NOTIFICATION_CHANNEL_ID"
        lateinit var socialNotificationChannel: NotificationChannel // Kênh thông báo xã hội
    }

    override fun getAppContext(): Context {
        return this
    }

    fun getNotificationManager(): NotificationManager? {
        return notificationManager
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine {
        return PictureSelectorEngineImp()
    }

    fun setTheme(theme: Boolean){
        if (theme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}