package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/12/2022
 */
class AvatarChat {

    @SerializedName("media_id")
    var mediaId : String = ""

    @SerializedName("type")
    var type : Int = 0

    @SerializedName("created_at")
    var createdAt : String = ""

    @SerializedName("original")
    var original : Original = Original()

    @SerializedName("medium")
    var medium : Medium = Medium()

    @SerializedName("thumb")
    var thumb : Thumb = Thumb()
}