package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 02/01/2023
 */
class ChatSocket {

    @SerializedName("message_id")
    var messageId: String = ""

    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("message")
    var message: String = ""

    @SerializedName("message_reply_id")
    var messageReplyId: String = ""

    @SerializedName("tag")
    var tag: ArrayList<UserTag> = ArrayList()

    @SerializedName("timestamp")
    var timestamp: String = ""

    @SerializedName("user")
    var user: UserRecently = UserRecently()


    @SerializedName("type")
    var type: Int = 0

    @SerializedName("thumb")
    var thumb: Thumb = Thumb()

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt: String = ""

    @SerializedName("media")
    var media: ArrayList<MediaList> = ArrayList()
}