package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Nguyễn Trọng Luân
 * @Date: 14/10/2022
 */
class RestaurantType: java.io.Serializable {

    @SerializedName("id")
    var id: Int ?= 0

    @SerializedName("name")
    var name: String ?= ""

}
