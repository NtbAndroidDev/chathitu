package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName
import com.hjq.http.annotation.HttpRename

class Permission {
    @HttpRename("user_id")
    var userId : Int = 0

    @SerializedName("permission")
    var permission : Int = 1
}