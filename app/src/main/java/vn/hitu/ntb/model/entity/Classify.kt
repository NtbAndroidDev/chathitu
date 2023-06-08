package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/12/2022
 */
class Classify {
    @SerializedName("id")
    var id  = ""

    @SerializedName("name")
    var name : String = ""

    @SerializedName("color")
    var color : String = ""
}