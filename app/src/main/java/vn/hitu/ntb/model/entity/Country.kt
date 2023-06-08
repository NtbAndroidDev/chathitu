package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class Country {
    @SerializedName("id")
    var id : Int? = 0

    @SerializedName("name")
    var name = ""
}