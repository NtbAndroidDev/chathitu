package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Phạm Văn Nhân
 * @Date: 29/09/2022
 */
class Media {

    @SerializedName("link_original")
    var linkOriginal : String? = ""

    @SerializedName("link_medium")
    var linkMedium : String? = ""

    @SerializedName("link_thumb")
    var linkThumb : String? = ""
}