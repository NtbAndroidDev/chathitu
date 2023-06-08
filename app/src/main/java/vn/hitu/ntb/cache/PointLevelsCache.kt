package vn.hitu.ntb.cache

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.PointBonusLevel
import java.lang.reflect.Type

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 23/12/2022
 */
object PointLevelsCache {
    var mPointLevels = ArrayList<PointBonusLevel>()
    private val mmkv: MMKV = MMKV.mmkvWithID("cache_point_bonus_level")

    fun savePointLevel(data: ArrayList<PointBonusLevel>) {
        try {
            mmkv.remove(AppConstants.CACHE_POINT_BONUS_LEVEL)
            mmkv.putString(AppConstants.CACHE_POINT_BONUS_LEVEL, Gson().toJson(data))
                .commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getPointLevel(): ArrayList<PointBonusLevel> {
        try {
            val pointBonusLevelType: Type = object : TypeToken<ArrayList<PointBonusLevel>>() {}.type
            mPointLevels = Gson().fromJson(
                mmkv.getString(AppConstants.CACHE_POINT_BONUS_LEVEL, ""),
                pointBonusLevelType
            )
        } catch (e: Exception) {
            e.stackTrace
        }
        return mPointLevels
    }
}