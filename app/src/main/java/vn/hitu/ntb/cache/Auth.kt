package vn.hitu.ntb.cache

import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import vn.hitu.ntb.model.entity.User

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 03/10/2022
 */
object Auth {

    private var mAuth = ""

    private val mmkv: MMKV = MMKV.mmkvWithID("mAuth")

    fun getAuth(): String {
        try {
            mAuth = mmkv.getString("mAuth", "")!!
        } catch (e: Exception) {
            e.stackTrace
        }
        return mAuth
    }

    fun saveAuth(mAuth: String) {
        try {
            mmkv.remove("mAuth")
            mmkv.putString("mAuth", mAuth).commit()
        } catch (e: Exception) {
            e.stackTrace
        }
    }




}
