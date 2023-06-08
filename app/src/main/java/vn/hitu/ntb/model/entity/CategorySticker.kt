package vn.hitu.ntb.model.entity

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
@Entity(tableName = "sticker")
class CategorySticker {

    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @ColumnInfo(name = "category_sticker_id")
    @SerializedName("category_sticker_id")
    var categoryStickerId = ""

    @ColumnInfo(name = "sticker_id")
    @SerializedName("sticker_id")
    var stickerId = ""

    @ColumnInfo(name = "media")
    @SerializedName("media")
    @TypeConverters(LocalMediaListDBConverters::class)
    var media = MediaList()

    @ColumnInfo(name = "sticker")
    @SerializedName("sticker")
    @TypeConverters(CategorySticker::class)
    var sticker : ArrayList<CategorySticker> = ArrayList()

    @TypeConverter
    fun fromJson(stat: ArrayList<CategorySticker>): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toJson(json: String): ArrayList<CategorySticker> {
        val token: TypeToken<ArrayList<CategorySticker>> = object : TypeToken<ArrayList<CategorySticker>>() {}
        return Gson().fromJson(json, token.type)
    }
}