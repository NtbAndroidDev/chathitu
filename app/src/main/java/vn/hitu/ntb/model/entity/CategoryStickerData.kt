package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/1/2023
 */
class CategoryStickerData {
    @SerializedName("total_record")
    var totalRecord = 0

    @SerializedName("limit")
    var limit = 0

    @SerializedName("list")
    var list = ArrayList<CategorySticker>()
}