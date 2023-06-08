package vn.hitu.ntb.model.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.hjq.http.annotation.HttpRename

class TagNewsFeed {
    @SerializedName("key")
    var key: String? = ""

    @HttpRename("user_id")
    var userId: Long = 0

    @TypeConverter
    fun fromTagNewsFeedJson(stat: List<TagNewsFeed>): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toTagNewsFeedJson(json: String): ArrayList<TagNewsFeed> {
        val token: TypeToken<ArrayList<TagNewsFeed>> = object : TypeToken<ArrayList<TagNewsFeed>>() {}
        return Gson().fromJson(json, token.type)
    }
}