package vn.hitu.ntb.cache

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.ImageClip

object ImageClipDetectCache {
    var data = ImageClip()
    private val mmkv: MMKV = MMKV.mmkvWithID("cache_image_clip")

    fun saveDataImageDetect(data: ImageClip) {
        try {
            mmkv.remove(AppConstants.CACHE_IMAGE_DETECT)
            mmkv.putString(AppConstants.CACHE_IMAGE_DETECT, Gson().toJson(data))
                .commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getImageDetect(): ImageClip {
        try {
            data = Gson().fromJson(
                mmkv.getString(AppConstants.CACHE_IMAGE_DETECT, ""), ImageClip::class.java
            )
        } catch (e: Exception) {
            e.stackTrace
        }
        return data
    }
}