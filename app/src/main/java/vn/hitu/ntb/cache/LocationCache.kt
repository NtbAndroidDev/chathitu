package vn.hitu.ntb.cache

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.UserLocation

/**
 * @Author: Nguyen Trong Luan
 * @Date: 12/11/2022
 */
object LocationCache {

    private var mUserLocation: UserLocation = UserLocation()
    private val mmkv: MMKV = MMKV.mmkvWithID("cache_location")
    fun saveLocation(userLocation: UserLocation?) {
        try {
            mmkv.remove(AppConstants.CACHE_LOCATION)
            mmkv.putString(AppConstants.CACHE_LOCATION, Gson().toJson(userLocation)).commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getLocation(): UserLocation {
        try {
            mUserLocation = Gson().fromJson(mmkv.getString(AppConstants.CACHE_LOCATION, ""), UserLocation::class.java)
        } catch (e: Exception) {
            e.stackTrace
        }
        return mUserLocation
    }
}