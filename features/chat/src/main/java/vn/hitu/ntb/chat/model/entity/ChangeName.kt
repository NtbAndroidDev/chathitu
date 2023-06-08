package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class ChangeName {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("name")
    var name: String = ""


}