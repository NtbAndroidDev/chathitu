package vn.hitu.ntb.chat.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.aghajari.emojiview.AXEmojiManager
import com.aghajari.emojiview.listener.SimplePopupAdapter
import com.aghajari.emojiview.search.AXEmojiSearchView
import com.aghajari.emojiview.view.AXEmojiPager
import com.aghajari.emojiview.view.AXEmojiPopupLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.ui.PlayerView
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.app.AppApplication.Companion.socketChat
import vn.hitu.ntb.chat.R
import vn.hitu.ntb.model.entity.UserTag
import vn.hitu.ntb.other.sticker.UI
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: Phạm Văn Nhân
 * @Date: 05/10/2022
 */
object ChatUtils {

    private var isShowing = false
    var on_off_audio = false

    fun setupEmojiSticker(
        activity: Activity,
        view: EditText,
        imageViewEmoji: ImageButton,
        imageViewMore: ImageButton,
        emojiPopupLayout: AXEmojiPopupLayout,
        extension: LinearLayout,
        lnMic: LinearLayout
    ) {
        val emojiPager: AXEmojiPager = UI.loadView(activity, view)
        // create emoji popup
        emojiPopupLayout.initPopupView(emojiPager)
        emojiPopupLayout.isPopupAnimationEnabled = true
        emojiPopupLayout.popupAnimationDuration = 250
        emojiPopupLayout.isSearchViewAnimationEnabled = true
        emojiPopupLayout.searchViewAnimationDuration = 250

        // SearchView
        if (AXEmojiManager.isAXEmojiView(emojiPager.getPage(0))) {
            emojiPopupLayout.searchView = AXEmojiSearchView(activity, emojiPager.getPage(0))
            emojiPager.setOnFooterItemClicked { _: View?, leftIcon: Boolean -> if (leftIcon) emojiPopupLayout.showSearchView() }
        }
        emojiPopupLayout.hideAndOpenKeyboard()

        view.setOnClickListener {
            if (emojiPopupLayout.visibility == View.GONE) {
                emojiPopupLayout.visibility = View.VISIBLE
            }
            emojiPopupLayout.openKeyboard()
            if (extension.visibility == View.VISIBLE) {
                extension.visibility = View.GONE
            }
        }

        imageViewEmoji.setOnClickListener {
            imageViewMore.isSelected = false
            if (extension.visibility == View.VISIBLE) {
                extension.visibility = View.GONE
            }
            if (lnMic.visibility == View.VISIBLE) {
                lnMic.visibility = View.GONE
            }
            if (emojiPopupLayout.visibility == View.GONE) {
                emojiPopupLayout.visibility = View.VISIBLE
            }
            if (isShowing) {
                emojiPopupLayout.openKeyboard()
            } else {
                emojiPopupLayout.show()
            }
        }

        emojiPopupLayout.setPopupListener(object : SimplePopupAdapter() {
            override fun onShow() {
                updateButton(true)
            }

            override fun onDismiss() {
                updateButton(false)
            }

            override fun onKeyboardOpened(height: Int) {
                updateButton(false)
            }

            override fun onKeyboardClosed() {
                updateButton(emojiPopupLayout.isShowing)
            }

            private fun updateButton(emoji: Boolean) {
                if (isShowing == emoji)
                    return
                isShowing = emoji
                imageViewEmoji.isSelected = emoji
            }
        })


    }

    fun emitSocket(key: String?, ojb: Any?) {
        try {
            val gson = Gson()
            Timber.e("%s : %s", key, gson.toJson(ojb))
            socketChat!!.emit(key, JSONObject(gson.toJson(ojb)))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun setTagNameFromKeyTagName(message: String, list: ArrayList<UserTag>): String {
        list.clear()
        var message = message
        for (tag in list) {
            val tagName: String =
                AppApplication.instance!!.appContext.getString(R.string.font_tag_name_start) + "@" + tag.fullName + "</font>" + " "
            message = message.replace(tag.key, tagName)
        }
        return message
    }

    fun setTagNameMessageWhenSend(message: String, list: ArrayList<UserTag>): String {
        var message = message
        for (tag in list) {
            message = message.replace("@" + tag.fullName, tag.key)
        }
        return message
    }

    //    fun resizeImage(url: String, view: ImageView) {
//        try {
//            var retryCount = 0
//            val imageLoader = ImageLoader(view.context)
//            val request = ImageRequest.Builder(view.context)
//                .data(if (!url.contains("/")) AppUtils.getLinkPhoto(url) else url)
//                .error(vn.hitu.ntb.R.drawable.ic_default)
//                .placeholder(vn.hitu.ntb.R.drawable.ic_default)
//                .memoryCacheKey(if (!url.contains("/")) AppUtils.getLinkPhoto(url) else url)
//                .diskCacheKey(if (!url.contains("/")) AppUtils.getLinkPhoto(url) else url)
//                .diskCachePolicy(CachePolicy.ENABLED)
//                .memoryCachePolicy(CachePolicy.ENABLED)
//                .listener(
//                    onError = { request: ImageRequest, errorResult: ErrorResult ->
//                        Timber.e("Lỗi hình ${if (!url.contains("/")) AppUtils.getLinkPhoto(url) else url}")
//                        // Handle error
//                        if (retryCount < 3) {
//                            // The image failed to load, so we'll try again
//                            retryCount++
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                imageLoader.enqueue(
//                                    request
//                                )
//                            }, 1000)
//                        } else {
//                            view.setImageDrawable(errorResult.drawable)
//                        }
//                    },
//                    onSuccess = { _: ImageRequest, successResult: SuccessResult ->
//                        val bitmap: Bitmap = resizeBitmap(
//                            successResult.drawable.toBitmap(),
//                            AppApplication.widthDevice * 2 / 3
//                        )!!
//                        view.layoutParams.height = bitmap.height
//                        view.layoutParams.width = bitmap.width
//                        view.setImageBitmap(bitmap)
//                    })
//                .build()
//            imageLoader.enqueue(request)
//        } catch (ignored: Exception) {
//            Timber.d("Load hình lỗi")
//        }
//    }
    fun resizeImage(url: String, view: ImageView) {
        try {
            //Link live
            if (!url.contains("/")) {
                Glide.with(view.context)
                    .asBitmap()
                    .load(AppUtils.getLinkPhoto(url, "images"))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap : %s", resource.width)
                            Timber.d("height Bitmap : %s", resource.height)
                            view.layoutParams.height = bitmap.height
                            view.layoutParams.width = bitmap.width
                            view.setImageBitmap(bitmap)
                        }
                    })
            } else {
                Glide.with(view.context)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap local: %s", resource.width)
                            Timber.d("height Bitmap local: %s", resource.height)
                            view.layoutParams.height = bitmap.height
                            view.layoutParams.width = bitmap.width
                            view.setImageBitmap(bitmap)
                        }
                    })
            }
        } catch (ignored: Exception) {
        }
    }

    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap? {
        return try {
            if (source.height > source.width) {
                val targetHeight = maxLength + maxLength / 3
                if (source.height <= targetHeight) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                var targetWidth = (targetHeight * aspectRatio).toInt()
                if (targetWidth > maxLength) {
                    targetWidth = maxLength
                }
                Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
            } else if (source.height == source.width) {
                Bitmap.createScaledBitmap(source, maxLength, maxLength, false)
            } else {
                if (source.width <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: java.lang.Exception) {
            source
        }
    }

    fun loadPhotoLocal(view: ImageView, url: String) {
        Glide.with(view).load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop().apply(
                RequestOptions().placeholder(R.drawable.ic_empty_media)
                    .error(R.drawable.ic_empty_media)
            ).into(view)
    }

    fun loadPhoto(view: ImageView, url: String) {
        Glide.with(view).load(AppUtils.getLinkPhoto(url, "images"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop().apply(
                RequestOptions().placeholder(R.drawable.ic_empty_media)
                    .error(R.drawable.ic_empty_media)
            ).into(view)
    }


    fun resizeVideo(url: String, imageView: ImageView) {
        try {
            //Link live
            if (!url.contains("/")) {
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(AppUtils.getLinkPhoto(url, "videos"))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap : %s", resource.width)
                            Timber.d("height Bitmap : %s", resource.height)
                            imageView.layoutParams.height = bitmap.height
                            imageView.layoutParams.width = bitmap.width
                            imageView.setImageBitmap(bitmap)
                        }
                    })
            } else {
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap local: %s", resource.width)
                            Timber.d("height Bitmap local: %s", resource.height)
                            imageView.layoutParams.height = bitmap.height
                            imageView.layoutParams.width = bitmap.width
                            imageView.setImageBitmap(bitmap)
                        }
                    })
            }
        } catch (ignored: Exception) {
        }
    }

    fun resizeBitmapVideo(source: Bitmap, maxLength: Int): Bitmap? {
        return try {
            if (source.height > source.width) {
                val targetHeight = maxLength + maxLength / 3
                if (source.height <= targetHeight) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                var targetWidth = (targetHeight * aspectRatio).toInt()
                if (targetWidth > maxLength) {
                    targetWidth = maxLength
                }
                Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
            } else if (source.height == source.width) {
                Bitmap.createScaledBitmap(source, maxLength, maxLength, false)
            } else {
                if (source.width <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: java.lang.Exception) {
            source
        }
    }

    fun getViewHeightOrWidth(view: View, heightOrWidth: Int): Int {
        val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val deviceWidth: Int
        val size = Point()
        display.getSize(size)
        deviceWidth = size.x
        val widthMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthMeasureSpec, heightMeasureSpec)
        return if (heightOrWidth == 0) {
            view.measuredHeight
        } else view.measuredWidth
    }

    fun getSizeBitmap(url: String, imageView: PlayerView) {
        try {
            //Link live
            if (!url.contains("/")) {
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(AppUtils.getLinkPhoto(url, "videos"))
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap : %s", resource.width)
                            Timber.d("height Bitmap : %s", resource.height)
                            imageView.layoutParams.height = bitmap.height
                            imageView.layoutParams.width = bitmap.width
                            val vto = imageView.viewTreeObserver
                            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    // Làm gì đó với chiều cao
                                    val layoutParams = imageView.layoutParams as ViewGroup.MarginLayoutParams
                                    layoutParams.height = bitmap.height
                                    layoutParams.width = bitmap.width
                                    imageView.layoutParams = layoutParams


                                    imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)


                                }
                            })
                        }
                    })
            } else {
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val bitmap: Bitmap = resizeBitmap(
                                resource,
                                AppApplication.widthDevice * 2 / 3
                            )!!
                            Timber.d("width Bitmap local: %s", resource.width)
                            Timber.d("height Bitmap local: %s", resource.height)
                            imageView.layoutParams.height = bitmap.height
                            imageView.layoutParams.width = bitmap.width
                        }
                    })
            }
        } catch (ignored: Exception) {
        }
    }

}