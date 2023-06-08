package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 28/12/2022
 */
class UserTag {
    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("nick_name")
    var nickName: String = ""

    @SerializedName("key")
    var key: String = ""
}