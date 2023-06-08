package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName
import com.hjq.http.annotation.HttpRename

/**
 * @Author:Hồ Quang Tùng
 * @Date: 29/11/2022
 */
class Medias {
    @SerializedName("name")
    var name: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("size")
    var size: Long = 0

    @SerializedName("width")
    var width: Int? = 0

    @SerializedName("height")
    var height: Int? = 0

    @HttpRename("is_keep")
    var isKeep: Int? = 0

    @SerializedName("realPath")
    var realPath: String? = ""


}