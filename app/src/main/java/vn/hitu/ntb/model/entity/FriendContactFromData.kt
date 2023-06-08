package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Phạm Văn Nhân
 * @Date: 04/10/2022
 * @Update: NGUYEN THANH BINH
 */

class FriendContactFromData {


    @SerializedName("list")
    var list = ArrayList<Friend>()

    @SerializedName("total_record")
    var totalRecord : Int? = 0


}