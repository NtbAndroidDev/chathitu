package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class Sender {
    @SerializedName("user_id")
    var userId = 0

    @SerializedName("full_name")
    var fullName = ""

    @SerializedName("avatar")
    var avatar = ""
}