package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/12/2022
 */
class UserRecently {
    @SerializedName("user_id")
    var userId : Int = 0

    @SerializedName("full_name")
    var fullName : String = ""

    @SerializedName("avatar")
    var avatar : String = ""
}