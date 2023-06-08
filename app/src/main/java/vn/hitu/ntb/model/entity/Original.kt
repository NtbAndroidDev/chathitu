package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class Original {
    @SerializedName("url")
    var url: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("size")
    var size: Int = 0

    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0


}