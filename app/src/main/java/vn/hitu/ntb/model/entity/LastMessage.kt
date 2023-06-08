package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class LastMessage{
    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("link")
    var link: ArrayList<String> = ArrayList()

    @SerializedName("message")
    var message: String = ""

    @SerializedName("message_id")
    var messageId: String = ""

    @SerializedName("message_reply_id")
    var messageReplyId: String = ""

    @SerializedName("no_of_reaction")
    var noOfReaction: String = ""

    @SerializedName("thumb")
    var thumb: Thumb = Thumb()

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("updated_at")
    var updatedAt: String = ""

    @SerializedName("user")
    var user: UserRecently = UserRecently()
}
   
