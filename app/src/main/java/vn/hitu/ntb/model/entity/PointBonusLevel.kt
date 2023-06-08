package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 12/12/2022
 */
class PointBonusLevel {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("point_target")
    var pointTarget: Int = 0

    @SerializedName("point_bonus")
    var pointBonus: Int = 0

    @SerializedName("total_point_bonus")
    var totalPointBonus: Int = 0
}