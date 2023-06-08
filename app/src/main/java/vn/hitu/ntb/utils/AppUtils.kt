package vn.hitu.ntb.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.text.ClipboardManager
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.*
import coil.load
import coil.transform.CircleCropTransformation
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import timber.log.Timber
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.model.entity.MediaList
import vn.hitu.ntb.model.entity.MediaShow
import vn.hitu.ntb.other.CenterLayoutManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.SecureRandom
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


object AppUtils {
//    private var isShowing = false

    /** Trả về tên thiết bị  */
    fun getDeviceName(): String {
        return Build.MANUFACTURER + " - " + Build.MODEL
    }

    fun getNameFileFormatTime(path: String?): String {
        return String.format(
            "%s.%s",
            System.currentTimeMillis(), getMimeType(path!!)
        )
    }

    /**
     * Check file name extension
     * */
    fun getMimeType(url: String): String? {
        try {
            return url.substring(url.lastIndexOf(".") + 1)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  địa chỉ ip
     */
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr: String = addr.hostAddress ?: ""
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                    0, delim
                                ).uppercase(Locale.getDefault())
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            //ex
        } // for now eat exceptions
        return ""
    }

    // Format tiền
    fun formatCurrencyDecimal(number: Float?): String {
        return String.format("%,.0f", number)
    }

    fun getDecimalFormattedString(value: String): String {
        var valuetmp = value
        if (valuetmp.contains("-")) {
            valuetmp = valuetmp.substring(1)
            val lst = StringTokenizer(valuetmp, ".")
            var str1 = valuetmp
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = StringBuilder()
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = StringBuilder(".")
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.isNotEmpty()) str3.append(".").append(str2)
                    return String.format("-%s", str3)
                }
                if (i == 3) {
                    str3.insert(0, ",")
                    i = 0
                }
                str3.insert(0, str1[k])
                i++
                k--
            }
        } else {
            val lst = StringTokenizer(valuetmp, ".")
            var str1 = valuetmp
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = StringBuilder()
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = StringBuilder(".")
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.isNotEmpty()) str3.append(".").append(str2)
                    return str3.toString()
                }
                if (i == 3) {
                    str3.insert(0, ",")
                    i = 0
                }
                str3.insert(0, str1[k])
                i++
                k--
            }
        }
    }

//    fun formatCurrencyFromDecimalToString(number: BigDecimal?): String {
//        return String.format("%,.0f", number)
//    }

    // Tải hình ảnh
    fun loadImageUser(view: ImageView, url: String) {
        if (url != "") {
            if (!url.contains("/")) {
                view!!.load(getLinkPhoto(url, "images"))
                {
                    crossfade(true)
                    placeholder(R.drawable.ic_user_default)
                    error(R.drawable.ic_user_default)
                    transformations(CircleCropTransformation())

                }
            } else {
                Glide.with(AppApplication.applicationContext().appContext)
                    .load(Uri.fromFile(File(url)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop().apply(
                        RequestOptions().placeholder(R.drawable.ic_user_default)
                            .error(R.drawable.ic_user_default)
                    ).transform(MultiTransformation(CenterCrop(), CircleCrop())).into(view)
            }
        }
    }

    fun loadAvatar(view: ImageView, url: String) {
        if (url != "avtdefault.jpg" && url != "")
        {
            view.load(getLinkPhoto(url, "images"))
            {
                crossfade(true)
                placeholder(R.drawable.ic_user_default)
                error(R.drawable.ic_user_default)
                transformations(CircleCropTransformation())
            }
        }
        else {
            Glide.with(view).load(Uri.fromFile(File(url)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().apply(
                    RequestOptions().placeholder(R.drawable.ic_user_default)
                        .error(R.drawable.ic_user_default)
                ).transform(MultiTransformation(CenterCrop(), CircleCrop())).into(view)
        }
    }


    fun loadAvatarGroup(view: ImageView, url: String) {
        if (url != "avtdefault.jpg" && url != "")
        {
            view.load(getLinkPhoto(url, "images")){
                crossfade(true)
                placeholder(vn.hitu.ntb.R.drawable.ic_empty_group)
                error(vn.hitu.ntb.R.drawable.ic_empty_group)
                transformations(CircleCropTransformation())
            }
        }
        else {
            Glide.with(view).load(Uri.fromFile(File("https://firebasestorage.googleapis.com/v0/b/chatapp-android17.appspot.com/o/images%2Favtgroup.jpg?alt=media&token=8cd1cd78-7da4-47c4-82d9-d51f9eee3b6f")))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().apply(
                    RequestOptions().placeholder(R.drawable.ic_empty_group)
                        .error(R.drawable.ic_empty_group)
                ).transform(MultiTransformation(CenterCrop(), CircleCrop())).into(view)
        }


    }






    fun loadImageLocal(view: ImageView, url: String) {
        view.load(getLinkPhoto(url, "images"))
        {
            crossfade(true)
            placeholder(R.drawable.ic_default)
            error(R.drawable.ic_default)
        }
    }









    fun loadMediaNewsFeed(view: ImageView, url: String) {
        if (view != null) {
            Glide.with(AppApplication.applicationContext())
                .load(getLinkPhoto(url, "images"))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(
                    RequestOptions().placeholder(R.color.black)
                        .error(R.color.black)
                )
                .into(view)
        }
    }

    fun getLinkPhoto(photo: String?, type : String): String {
        return String.format("%s%s?alt=media", "https://firebasestorage.googleapis.com/v0/b/chathitu-33168.appspot.com/o/$type%2F", photo)
    }

    private fun configRecyclerView(
        recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager?
    ) {
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(100)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerView.itemAnimator = DefaultItemAnimator()
        Objects.requireNonNull(recyclerView.itemAnimator)!!.changeDuration = 0
        (Objects.requireNonNull(recyclerView.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
            false
        recyclerView.isNestedScrollingEnabled = false
    }

    fun initRecyclerViewVertical(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        configRecyclerView(
            view, LinearLayoutManager(
                view.context, RecyclerView.VERTICAL, false
            )
        )
        view.adapter = adapter
    }
    fun initRecyclerViewVerticalEnd(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        configRecyclerView(
            view, LinearLayoutManager(
                view.context, RecyclerView.VERTICAL, false
            )
        )
        view.adapter = adapter
    }

    fun initRecyclerViewVertical(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        count: Int
    ) {
        configRecyclerView(view, GridLayoutManager(view.context, count))
        view.adapter = adapter
    }

    fun initRecyclerViewHorizontal(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        configRecyclerView(
            view, LinearLayoutManager(
                view.context, RecyclerView.HORIZONTAL, false
            )
        )
        view.adapter = adapter
    }

    fun initRecyclerViewHorizontal(
        view: RecyclerView,
        adapter: RecyclerView.Adapter<*>?,
        count: Int
    ) {
        configRecyclerView(view, GridLayoutManager(view.context, count))
        view.adapter = adapter
    }

    fun initRecyclerViewReverse(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        val preCachingLayoutManager = CenterLayoutManager(
            view.context,
            RecyclerView.VERTICAL,
            true
        )
        configRecyclerView(view, preCachingLayoutManager)
        view.adapter = adapter
    }

    //format điểm đánh giá nhà hàng
    //ví dụ: 5.0 ---> 5, 5.1, 5.2 ---> 5.1/5.2
    fun formatDoubleToString(value: Double): String {
        val s: String = if (value.toInt().toDouble().compareTo(value) == 0) {
            java.lang.String.format(Locale.getDefault(), "%s", value.toInt())
        } else {
            java.lang.String.format(Locale.getDefault(), "%s", value)
        }
        return s
    }

    fun fromHtml(string: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(string)
        }
    }

    fun setSnackBarError(view: View?, s: String?) {
        val snackBar = TSnackbar.make(view!!, s!!, TSnackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.RED)

        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 88, 0, 0)
        snackBarView.layoutParams = lp
        val textView =
            snackBarView.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    @SuppressLint("ResourceAsColor")
    fun setSnackBarSuccess(view: View?, s: String?) {
        val snackBar = TSnackbar.make(view!!, s!!, TSnackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundResource(R.color.color_success)

        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 88, 0, 0)
        snackBarView.layoutParams = lp
        val textView =
            snackBarView.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    //Round double to 2 decimal
    fun roundOffDecimal(numInDouble: Double): Double {
        return BigDecimal(numInDouble.toString()).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun getNameFileToPath(path: String): String? {
        return path.substring(path.lastIndexOf("/") + 1)
    }


    @Throws(IOException::class)
    fun copyFile(source: File?, destination: File?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(FileInputStream(source), FileOutputStream(destination))
        }
    }

    fun checkMimeTypeVideo(url: String?): Boolean {
        return getMimeType(url!!) == "mp4" || getMimeType(
            url
        ) == "MP4" || getMimeType(url) == "MOV" || getMimeType(url) == "mov"
    }

    fun checkMimeTypeImage(url: String?): Boolean {
        return getMimeType(url!!) == "png" || getMimeType(
            url
        ) == "jpg" || getMimeType(url) == "jpeg" || getMimeType(
            url
        ) == "gif"
    }


    fun showMediaNewsFeed(context: Context, data: ArrayList<MediaList>, position: Int) {
        val medias: ArrayList<MediaShow> = ArrayList()
        for (i in data.indices) {
            medias.add(
                MediaShow(
                    data[i].original.url,
                    data[i].pathLocal,
                    data[i].original.name,
                    data[i].type
                )
            )
        }

        val intent = Intent(
            context,
            Class.forName(ModuleClassConstants.MEDIA_SLIDER_ACTIVITY)
        )
        val bundle = Bundle()
        bundle.putString(AppConstants.DATA_MEDIA, Gson().toJson(medias))
        bundle.putInt(AppConstants.POSITION_MEDIA, position)

        if (data.size == 1 && position == 0) {
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, false)
        } else
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, true)

        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    fun showMediaLocal(context: Context, data: ArrayList<MediaList>, position: Int) {
        var medias: ArrayList<MediaShow> = ArrayList()
        for (i in data.indices) {
            medias.add(MediaShow(data[i].original.url, data[i].original.name, data[i].type))
        }

        val intent = Intent(
            context,
            Class.forName(ModuleClassConstants.MEDIA_SLIDER_ACTIVITY)
        )
        val bundle = Bundle()
        bundle.putString(AppConstants.DATA_MEDIA, Gson().toJson(medias))
        bundle.putInt(AppConstants.POSITION_MEDIA, position)

        if (data.size == 1 && position == 0) {
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, false)
        } else
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, true)

        intent.putExtras(bundle)
        context.startActivity(intent)
    }


    @SuppressLint("SuspiciousIndentation")
    fun showMediaNewsFeedComment(context: Context, data: MediaList, position: Int) {
        var medias: ArrayList<MediaShow> = ArrayList()
        medias.add(MediaShow(data.original.url, data.original.name, data.type))
        val intent = Intent(
            context,
            Class.forName(ModuleClassConstants.MEDIA_SLIDER_ACTIVITY)
        )
        val bundle = Bundle()
        bundle.putString(AppConstants.DATA_MEDIA, Gson().toJson(medias))
        bundle.putInt(AppConstants.POSITION_MEDIA, position)

        if (position == 0) {
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, false)
        } else
            bundle.putBoolean(AppConstants.MEDIA_COUNT_VISIBLE, true)

        intent.putExtras(bundle)
        context.startActivity(intent)
    }


    fun calculateTotalPage(totalRecord: Int): Int {
        return if (totalRecord % 20 == 0) {
            (totalRecord / 20)
        } else {
            (totalRecord / 20) + 1
        }
    }

    fun random(): String {
        val generator = Random()
        val randomStringBuilder = java.lang.StringBuilder()
        val randomLength = generator.nextInt(15)
        var tempChar: Char
        for (i in 0 until randomLength) {
            tempChar = (generator.nextInt(96) + 32).toChar()
            randomStringBuilder.append(tempChar)
        }
        return randomStringBuilder.toString()
    }

    fun resetExternalStorageMedia(context: Context): Boolean {
        if (Environment.isExternalStorageEmulated()) return false
        val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory())
        val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, uri)
        context.sendBroadcast(intent)
        return true
    }

    fun resizeImage800(): RequestOptions? {
        return RequestOptions()
            .fitCenter()
            .override(800, 800)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun prettyCount(number: Number): String? {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(
                numValue / 10.0.pow((base * 3).toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun getRandomString(len: Int): String {
        val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        val rnd = SecureRandom()
        val sb = java.lang.StringBuilder(len)
        for (i in 0 until len) sb.append(AB[rnd.nextInt(AB.length)])
        return sb.toString()
    }

    fun getFirstLetterEachWord(string: String): String {
        var resutl = ""
        val stringArray = string.split("(?<=[\\S])[\\S]*\\s*").toTypedArray()
        for (s in stringArray) {
            resutl = String.format("%s%s", resutl, s)
        }
        return resutl
    }

    fun removeVietnameseFromString(str: String?): String {
        val slug: String = try {
            val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
            val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            pattern.matcher(temp).replaceAll("")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
        return slug
    }


    fun getStorageDir(fileName: String?): String {
        val file = File(
            Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_DOWNLOADS + File.separator + AppConstants.FOLDER_APP
        )
        if (!file.mkdirs()) {
            file.mkdirs()
        }
        //create folder
        return file.absolutePath + File.separator + getNameFileToPath(fileName!!)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setClipboard(text: String?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard =
                AppApplication.instance!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard =
                AppApplication.instance!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    fun resizeImageClip(url: String?, view: ImageView) {
        try {
            Glide.with(view)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        val bitmap = Bitmap.createScaledBitmap(
                            resource,
                            AppApplication.widthDevice / 4,
                            AppApplication.heightDevice / 4,
                            false
                        )
                        view.layoutParams.height = bitmap.height
                        view.layoutParams.width = bitmap.width
                        view.setImageBitmap(bitmap)
                    }
                })
        } catch (ignored: java.lang.Exception) {
        }
    }

    fun formatTimeWithUTC(str_date: String): String {
        val OLD_FORMAT = "MM-dd-yyyy HH:mm:ss"
        val NEW_FORMAT = "HH:mm dd-MM-yyyy"

        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat(OLD_FORMAT)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = try {
            df.parse(str_date) as Date
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
        df.timeZone = TimeZone.getDefault()
        val formattedDate = df.format(Objects.requireNonNull(date))

        var newDateString = ""
        try {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat(OLD_FORMAT)
            val d = sdf.parse(formattedDate)
            sdf.applyPattern(NEW_FORMAT)
            newDateString = sdf.format(Objects.requireNonNull(d))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newDateString
    }

    fun copyText(activity: Activity, text: String?) {
        val clipboard =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("\"text\"", text)
        clipboard.setPrimaryClip(clip)
    }

    fun downloadFile(activity: Context, file: String?) {
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(getLinkPhoto(file, "files"))
        val request = DownloadManager.Request(uri)
        request.setTitle(getNameFileToPath(getLinkPhoto(file, "files")))
        request.setDescription("Tải về")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS.toString(),
            "hitu" + File.separator + getNameFileToPath(
                getLinkPhoto(file, "files")
            )!!.replace("%20", "")
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
    }

    fun getNameNoType(path: String): String {
        val filename = path.substring(path.lastIndexOf("/") + 1)
        val parts = filename.split("\\.").toTypedArray()
        return parts[0]
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    fun getVideo(url: String, context: Context): String? {
        var link: String? = ""
        val a: Int
        val b: Int
        if (url.contains(context.getString(R.string.link_youtube_1))) {
            a = url.indexOf(".be/")
            link = url.substring(a + 4)
        } else if (url.contains(context.getString(R.string.link_youtube_4))) {
            a = url.indexOf("?v=")
            if (url.contains("&")) {
                b = url.indexOf("&")
                link = url.substring(a + 3, b)
            } else link = url.substring(a + 3)
        } else if (url.contains(context.getString(R.string.link_youtube_3))) {
            a = url.indexOf("?")
            b = url.indexOf("shorts/")
            link = url.substring(b + 7, a)
        }
        Timber.d("load link id : ")
        Timber.d(link)
        return link
    }

    private fun isNullOrEmpty(str: String?): Boolean {
        return str.isNullOrEmpty()
    }

    fun getBitmap(urlImage: String?, context: Context): Bitmap {
        val bitmapAvatar = if (isNullOrEmpty(getLinkPhoto(urlImage, "images"))) {
            Glide.with(context)
                .asBitmap()
                .load(
                    R.drawable.ic_user_default
                )
                .circleCrop()
                .placeholder(R.drawable.ic_user_default)
                .error(R.drawable.ic_user_default)
                .submit(100, 100)
                .get()
        } else {
            try {
                Glide.with(context)
                    .asBitmap()
                    .load(getLinkPhoto(urlImage, "images"))
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(
                        RequestOptions().placeholder(R.drawable.ic_user_default)
                            .error(R.drawable.ic_user_default)
                    )
                    .submit(100, 100)
                    .get()
            } catch (e: Exception) {
                Glide.with(context)
                    .asBitmap()
                    .load(
                        R.drawable.ic_user_default
                    )
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_default)
                    .error(R.drawable.ic_user_default)
                    .submit(100, 100)
                    .get()
            }
        }
        return bitmapAvatar
    }
    fun removeAccent(s: String?): String? {
        val temp: String = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }
    fun getLastTime(time: String): String {
        var lastTime = time
        if (time.length < 8) {
            return lastTime
        }
        val pos = time.lastIndexOf(' ')
        if (pos != -1) {
            lastTime = time.substring(pos + 1, pos + 6)
        }
        return lastTime
    }
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }
    fun loadBackground(view: ImageView, url: String) {
        if (!url.contains("/")) {
            view.load(getLinkPhoto(url, "images"))
            {
                crossfade(true)
                placeholder(vn.hitu.ntb.R.drawable.bg_white_default)
                error(R.drawable.bg_white_default)
            }
        } else {
            Glide.with(view).load(Uri.fromFile(File(url)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(
                    RequestOptions().placeholder(vn.hitu.ntb.R.drawable.bg_white_default)
                        .error(R.drawable.bg_white_default)
                ).into(view)
        }
    }
    fun sendMessage(content: String, gId : String) {
        //path: ChatMessage
        val date = Date()
        val timestamp = Timestamp(date.time)
        val currentTime = timestamp.toString()
        val chat = ChatMessage(currentTime, content, Auth.getAuth(), "notification", "")
        val ref = FirebaseDatabase.getInstance().reference
        val messageId = ref.child("ChatMessage").child(gId).push().key
        chat.messageId = messageId!!
        val messUpdates: MutableMap<String, Any> = java.util.HashMap()
        val messValues = chat.toMap()
        //path/ChatMessage/idGroup/messageId
        messUpdates["/ChatMessage/${gId}/$messageId"] = messValues
        ref.updateChildren(messUpdates)
        val refGroups = FirebaseDatabase.getInstance().getReference("Groups").child(gId)
        val refMessageGroup = refGroups.child("lastMessage")
        val refTimeGroup = refGroups.child("lastTime")
        refTimeGroup.setValue(chat.messageTime)
        val notification = when (chat.typeMessage) {
            "notification" -> {
                refMessageGroup.setValue("notification")
                content
                // :(
            }

            else -> {
                refMessageGroup.setValue(chat.message)
                chat.message
                // :(
            }
        }
    }
}