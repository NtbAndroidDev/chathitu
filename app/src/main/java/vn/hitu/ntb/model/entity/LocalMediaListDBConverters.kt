package vn.hitu.ntb.model.entity

import androidx.room.TypeConverter
import com.google.gson.Gson

class LocalMediaListDBConverters {
    @TypeConverter
    fun fromMediaListJson(stat: MediaList): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toMediaListJson(json: String): MediaList {
        return Gson().fromJson(json, MediaList::class.java)
    }
}