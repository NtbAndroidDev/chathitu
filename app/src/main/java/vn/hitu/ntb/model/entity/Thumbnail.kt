package vn.hitu.ntb.model.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Thumbnail {
    @SerializedName("domain")
    var domain: String = ""

    @SerializedName("title")
    var title: String = ""

    @SerializedName("description")
    var description: String = ""

    @SerializedName("logo")
    var logo: String = ""

    @SerializedName("url")
    var url: String = ""

    @TypeConverter
    fun fromThumbnailJson(stat: Thumbnail): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toThumbnailJson(json: String): Thumbnail {
        return Gson().fromJson(json, Thumbnail::class.java)
    }
}