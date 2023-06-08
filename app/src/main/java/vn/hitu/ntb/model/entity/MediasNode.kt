package vn.hitu.ntb.model.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.hjq.http.annotation.HttpRename

class MediasNode {
    @SerializedName("content")
    var content: String? = ""

    @HttpRename("media_id")
    var mediaId: String? = ""

    @TypeConverter
    fun fromMediasNodeJson(stat: ArrayList<MediasNode>): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toMediasNodeJson(json: String): ArrayList<MediasNode> {
        val token: TypeToken<ArrayList<MediasNode>> = object : TypeToken<ArrayList<MediasNode>>() {}
        return Gson().fromJson(json, token.type)
    }
}