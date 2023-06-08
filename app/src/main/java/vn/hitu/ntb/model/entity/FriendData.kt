package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Phạm Văn Nhân
 * @Date: 04/10/2022
 */
class FriendData {
    @SerializedName("list")
    var list = ArrayList<Friend>()

    @SerializedName("total_record")
    var totalRecord : Int? = 0

    @SerializedName("limit")
    var limit : Int? = 0
}