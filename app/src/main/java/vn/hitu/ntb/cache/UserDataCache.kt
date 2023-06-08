package vn.hitu.ntb.cache

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.UserData

object UserDataCache {

    private var mUserInfo = UserData()

    private val mmkv: MMKV = MMKV.mmkvWithID("cache_user")

    fun getUser(): UserData {
        try {
            mUserInfo =
                Gson().fromJson(mmkv.getString(AppConstants.CACHE_USER, ""), UserData::class.java)
        } catch (e: Exception) {
            e.stackTrace
        }
        return mUserInfo
    }

    fun saveUser(userInfo: UserData) {
        try {
            mmkv.remove(AppConstants.CACHE_USER)
            mmkv.putString(AppConstants.CACHE_USER, Gson().toJson(userInfo)).commit()
        } catch (e: Exception) {
            e.stackTrace
        }
    }




    fun getNodeToken(): String {
        return  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhM2JkMzIxYi01MGZlLTQ2NmQtOTU4ZC0zZDIyY2ZlMGRjMTQiLCJpYXQiOjE2ODEzODU1MTEsInN1YiI6IjMiLCJpc3MiOiJURUNIUkVTIiwiZXhwIjoxNjk2OTM3NTExLCJ1c2VyX2lkIjoiOTg2MDAiLCJjdXJyZW50X3RpbWVzdGFtcCI6MTY4MTM4NTUxMjAyOX0.SRdmSynjLuBuDZ_-pXG7x7CToDDQKyPKBWQha6GxLXo"
    }
}
