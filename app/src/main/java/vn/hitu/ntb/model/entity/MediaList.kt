package vn.hitu.ntb.model.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


class MediaList {
    @SerializedName("media_id")
    var mediaId: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("original")
    var original: Original = Original()

    @SerializedName("medium")
    var medium: Medium = Medium()

    @SerializedName("thumb")
    var thumb: Thumb = Thumb()

    @SerializedName("position")
    var position: String = ""

    @SerializedName("path_local")
    var pathLocal = ""

    @SerializedName("stop")
    var stop: Boolean = false

    var seekTo = 0

    @TypeConverter
    fun fromMediaListJson(stat: ArrayList<MediaList>): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toMediaListJson(json: String): ArrayList<MediaList> {
        val token: TypeToken<ArrayList<MediaList>> = object : TypeToken<ArrayList<MediaList>>() {}
        return Gson().fromJson(json, token.type)
    }
}