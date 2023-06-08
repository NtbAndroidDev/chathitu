package vn.hitu.ntb.ui.activity

import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.GravityCompat
import com.gyf.immersionbar.ImmersionBar
import vn.hitu.ntb.R
import vn.hitu.ntb.aop.SingleClick
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.other.AppConfig
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import vn.hitu.ntb.databinding.CrashActivityBinding
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.min

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class CrashActivity : AppActivity() {

    companion object {

        private const val INTENT_KEY_IN_THROWABLE: String = "throwable"

        private val SYSTEM_PACKAGE_PREFIX_LIST: Array<String> = arrayOf("android", "com.android",
            "androidx", "com.google.android", "java", "javax", "dalvik", "kotlin")

        private val CODE_REGEX: Pattern = Pattern.compile("\\(\\w+\\.\\w+:\\d+\\)")

        fun start(application: Application, throwable: Throwable?) {
            if (throwable == null) {
                return
            }
            val intent = Intent(application, CrashActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_THROWABLE, throwable)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
        }
    }

    private lateinit var binding: CrashActivityBinding

    private var stackTrace: String? = null

    override fun initView() {
        setOnClickListener(R.id.iv_crash_info, R.id.iv_crash_share, R.id.iv_crash_restart)

        //Đặt chìm trong thanh trạng thái
        ImmersionBar.setTitleBar(this, findViewById(R.id.ll_crash_bar))
        ImmersionBar.setTitleBar(this, findViewById(R.id.ll_crash_info))
    }

    override fun initData() {
        val throwable: Throwable = getSerializable(INTENT_KEY_IN_THROWABLE) ?: return
        binding.tvCrashTitle.text = throwable.javaClass.simpleName
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        throwable.cause?.printStackTrace(printWriter)
        stackTrace = stringWriter.toString()
        val matcher: Matcher = CODE_REGEX.matcher(stackTrace!!)
        val spannable = SpannableStringBuilder(stackTrace)
        if (spannable.isNotEmpty()) {
            while (matcher.find()) {
                val start: Int = matcher.start() + "(".length
                val end: Int = matcher.end() - ")".length

                var codeColor: Int = Color.parseColor("#999999")
                val lineIndex: Int = stackTrace!!.lastIndexOf("at ", start)
                if (lineIndex != -1) {
                    val lineData: String = spannable.subSequence(lineIndex, start).toString()
                    if (TextUtils.isEmpty(lineData)) {
                        continue
                    }
                    var highlight = true
                    for (packagePrefix: String? in SYSTEM_PACKAGE_PREFIX_LIST) {
                        if (lineData.startsWith("at $packagePrefix")) {
                            highlight = false
                            break
                        }
                    }
                    if (highlight) {
                        codeColor = Color.parseColor("#287BDE")
                    }
                }

                spannable.setSpan(ForegroundColorSpan(codeColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.tvCrashMessage.text = spannable
        }
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val screenWidth: Int = displayMetrics.widthPixels
        val screenHeight: Int = displayMetrics.heightPixels
        val smallestWidth: Float = min(screenWidth, screenHeight) / displayMetrics.density
        val targetResource: String?
        when {
            displayMetrics.densityDpi > 480 -> {
                targetResource = "xxxhdpi"
            }
            displayMetrics.densityDpi > 320 -> {
                targetResource = "xxhdpi"
            }
            displayMetrics.densityDpi > 240 -> {
                targetResource = "xhdpi"
            }
            displayMetrics.densityDpi > 160 -> {
                targetResource = "hdpi"
            }
            displayMetrics.densityDpi > 120 -> {
                targetResource = "mdpi"
            }
            else -> {
                targetResource = "ldpi"
            }
        }
        val builder: StringBuilder = StringBuilder()
        builder.append("Thương hiệu thiết bị：\t").append(Build.BRAND)
            .append("\nMô hình thiết bị：\t").append(Build.MODEL)
            .append("\nLoại thiết bị：\t").append(if (isTablet()) "Máy tính bảng" else "Điện thoại")

        builder.append("\nĐộ rộng màn hình：\t").append(screenWidth).append(" x ").append(screenHeight)
            .append("\nMật độ màn hình：\t").append(displayMetrics.densityDpi)
            .append("\nĐộ phân giải：\t").append(displayMetrics.density)
            .append("\nTài nguyên：\t").append(targetResource)
            .append("\nChiều rộng tối thiểu：\t").append(smallestWidth.toInt())

        builder.append("\nPhiên bản android：\t").append(Build.VERSION.RELEASE)
            .append("\nAPI：\t").append(Build.VERSION.SDK_INT)
            .append("\nCPU：\t").append(Build.SUPPORTED_ABIS[0])

        builder.append("\nPhiên bản：\t").append(AppConfig.getVersionName())
            .append("\nMã phiên bản：\t").append(AppConfig.getVersionCode())

        try {
            val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            builder.append("\nCài đặt lần đầu tiên\n：\t")
                .append(dateFormat.format(Date(packageInfo.firstInstallTime)))
                .append("\nĐược cài đặt gần đây：\t").append(dateFormat.format(Date(packageInfo.lastUpdateTime)))
                .append("\nThời gian crash：\t").append(dateFormat.format(Date()))
            val permissions: MutableList<String> = mutableListOf(*packageInfo.requestedPermissions)
            if (permissions.contains(Permission.READ_EXTERNAL_STORAGE) ||
                permissions.contains(Permission.WRITE_EXTERNAL_STORAGE)) {
                builder.append("\nQuyền lưu trữ：\t").append(
                    if (XXPermissions.isGranted(this, *Permission.Group.STORAGE)) "Được phép" else "Không"
                )
            }
            if (permissions.contains(Permission.ACCESS_FINE_LOCATION) ||
                permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                builder.append("\nQuyền vị trí：\t")
                if (XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)) {
                    builder.append("Chính xác")
                } else {
                    when {
                        XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION) -> {
                            builder.append("Chính xác")
                        }
                        XXPermissions.isGranted(this, Permission.ACCESS_COARSE_LOCATION) -> {
                            builder.append("Xác xuất")
                        }
                        else -> {
                            builder.append("Không thu được")
                        }
                    }
                }
            }
            if (permissions.contains(Permission.CAMERA)) {
                builder.append("\ncho phép máy ảnh：\t")
                    .append(if (XXPermissions.isGranted(this, Permission.CAMERA)) "thu được" else "Không thu được")
            }
            if (permissions.contains(Permission.RECORD_AUDIO)) {
                builder.append("\nQuyền ghi âm：\t").append(
                    if (XXPermissions.isGranted(this, Permission.RECORD_AUDIO)) "thu được" else "Không thu được"
                )
            }
            if (permissions.contains(Permission.SYSTEM_ALERT_WINDOW)) {
                builder.append("\nQuyền đối với cửa sổ nổi：\t").append(
                    if (XXPermissions.isGranted(this, Permission.SYSTEM_ALERT_WINDOW)) "thu được" else "Không thu được"
                )
            }
            if (permissions.contains(Permission.REQUEST_INSTALL_PACKAGES)) {
                builder.append("\nCài đặt quyền gói：\t").append(
                    if (XXPermissions.isGranted(this, Permission.REQUEST_INSTALL_PACKAGES)) "thu được" else "Không thu được"
                )
            }
        } catch (_: PackageManager.NameNotFoundException) {

        }
    }

    @SingleClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_crash_info -> {
                binding.dlCrashDrawer.openDrawer(GravityCompat.START)
            }
            R.id.iv_crash_share -> {
                // 分享文本
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, stackTrace)
                startActivity(Intent.createChooser(intent, ""))
            }
            R.id.iv_crash_restart -> {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        // 重启应用
        RestartActivity.restart(this)
        finish()
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // Chỉ định màu nền của thanh điều hướng
            .navigationBarColor(R.color.white)
    }

    override fun getLayoutView(): View {
        binding = CrashActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * 判断当前设备是否是平板
     */
    fun isTablet(): Boolean {
        return ((resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }
}