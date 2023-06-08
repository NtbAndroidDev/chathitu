package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class MessageImage {
   @SerializedName("media")
   var media : ArrayList<String> = ArrayList()
}