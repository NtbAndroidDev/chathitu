package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class ChangeAvatar {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

}