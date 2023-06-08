package vn.hitu.ntb.model.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *    author : HỒ QUANG TÙNG
 *    time   : 2022/09/28
 *
 */
class Avatar: Serializable {
    @SerializedName("link_original")
    var linkOriginal : String? = ""

    @SerializedName("link_medium")
    var linkMedium : String? = ""

    @SerializedName("link_thumb")
    var linkThumb : String? = ""


    fun fromAvatarJson(stat: Avatar?): String? {
        return Gson().toJson(stat)
    }


    fun toAvatarJson(json: String?): Avatar? {
        return Gson().fromJson(
            json,
            Avatar::class.java
        )
    }
}