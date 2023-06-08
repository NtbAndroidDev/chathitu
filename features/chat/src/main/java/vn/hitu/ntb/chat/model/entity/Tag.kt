package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class Tag {
    @SerializedName("key")
    var key = ""

    @SerializedName("user_id")
    var userId = 0
}