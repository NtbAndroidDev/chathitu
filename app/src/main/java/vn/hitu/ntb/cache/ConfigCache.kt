package vn.hitu.ntb.cache

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.Config

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
object ConfigCache {

    var mConfig: Config = Config()

    private val mmkv: MMKV = MMKV.mmkvWithID("cache_config")

    fun saveConfig(config: Config?) {
        try {
            mmkv.remove(AppConstants.CACHE_CONFIG)
            mmkv.putString(AppConstants.CACHE_CONFIG, Gson().toJson(config)).commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getConfig(): Config {
        try {
            mConfig = Gson().fromJson(mmkv.getString(AppConstants.CACHE_CONFIG, ""), Config::class.java)
        } catch (e: Exception) {
            e.stackTrace
        }
        return mConfig
    }
}