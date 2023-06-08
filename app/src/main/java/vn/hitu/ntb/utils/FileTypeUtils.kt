package vn.hitu.ntb.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.luck.picture.lib.entity.LocalMedia
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.constants.UploadTypeConstants
import java.text.DecimalFormat
import java.util.ArrayList

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 3/21/2023
 */
object FileTypeUtils {
    fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return df.format((size / sizeKb).toDouble()) + " KB" else if (size < sizeGb) return df.format(
            (size / sizeMb).toDouble()
        ) + " MB" else if (size < sizeTerra) return df.format((size / sizeGb).toDouble()) + " GB"
        return ""
    }

    fun getMimeType(url: String): String {
        try {
            return url.substring(url.lastIndexOf(".") + 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setLogoImageToFile(context: Context, imageView: ImageView, typeMedia: String?) {
        when (typeMedia) {
            UploadTypeConstants.TYPE_JPG, UploadTypeConstants.TYPE_JPEG, UploadTypeConstants.TYPE_PNG, UploadTypeConstants.TYPE_SVG -> {
                imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_photo))
            }

            UploadTypeConstants.TYPE_DOCX -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_word))

            UploadTypeConstants.TYPE_AI -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_ai))

            UploadTypeConstants.TYPE_DMG -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_dmg))

            UploadTypeConstants.TYPE_XLSX -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_excel))

            UploadTypeConstants.TYPE_HTML -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_html))

            UploadTypeConstants.TYPE_MP3 -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_music))

            UploadTypeConstants.TYPE_PDF -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_pdf))

            UploadTypeConstants.TYPE_PPTX -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_powerpoint))

            UploadTypeConstants.TYPE_PSD -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_psd))

            UploadTypeConstants.TYPE_REC -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_record))

            UploadTypeConstants.TYPE_EXE -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_setup))

            UploadTypeConstants.TYPE_SKETCH -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_sketch))

            UploadTypeConstants.TYPE_TXT -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_txt))

            UploadTypeConstants.TYPE_MP4 -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_video))

            UploadTypeConstants.TYPE_XML -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_xml))

            UploadTypeConstants.TYPE_ZIP -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_zip))

            else -> imageView.setImageDrawable(context.getDrawable(R.drawable.icon_file_attach))
        }
    }

    fun checkFileSizeToUpload(listMedia: ArrayList<LocalMedia>, activity: AppActivity): Boolean {
        for (mediaItem in listMedia) {
            if (mediaItem.size > (100 * 1000000).toLong()) {//Max size: 100MB, 1MB = 10^6 long
//                activity.toast(activity.getString(R.string.max_size_image_video))
                return false
            }
        }

        return true
    }
}