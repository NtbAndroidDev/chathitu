package vn.hitu.ntb.chat.ui.activity


import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.chat.constants.ChatConstants
import vn.hitu.ntb.chat.constants.MessageChatConstants
import vn.hitu.ntb.chat.databinding.ActivityQrGroupBinding
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.GroupChat
import vn.hitu.ntb.model.entity.GroupData

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/12/2022
 */
class QrGroupActivity : AppActivity() {
    private lateinit var binding : ActivityQrGroupBinding
    var group = GroupData()

    override fun getLayoutView(): View {
        binding = ActivityQrGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        val bundleIntent = intent.extras
        if (bundleIntent != null) {
            if (bundleIntent.containsKey(MessageChatConstants.DATA_GROUP)) {

                group = Gson().fromJson(
                    bundleIntent.getString(MessageChatConstants.DATA_GROUP), GroupData::class.java
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        binding.tvLink.text = AppConstants.LINK_GROUP + group.gid
        binding.ivQR.setImageBitmap(create(group.gid))
        binding.btnCopy.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = binding.tvLink.text
            toast("Đã copy văn bản")
        }
    }
    private fun create(idGroup: String): Bitmap? {
        return try {

            val result: BitMatrix = QRCodeWriter().encode(
                String.format(
                    "%s:%s:%s:%s:QR_GROUP",
                    idGroup,
                    group.name, group.imageId, Gson().toJson(group.listUidMember)
                ), BarcodeFormat.QR_CODE, 1024, 1024
            )
            val bitmap: Bitmap = Bitmap.createBitmap(
                result.width,
                result.height,
                Bitmap.Config.ARGB_8888
            )
            for (y in 0 until result.height) {
                for (x in 0 until result.width) {
                    if (result.get(x, y)) {
                        bitmap.setPixel(x, y, Color.BLACK)
                    }
                }
            }
            bitmap
        } catch (e: WriterException) {
            Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        }
    }

}