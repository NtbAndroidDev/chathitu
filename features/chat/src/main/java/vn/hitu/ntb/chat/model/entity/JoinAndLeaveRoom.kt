package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class JoinAndLeaveRoom {
    @SerializedName("conversation_id")
    var conversationId: String = ""

    constructor(conversationId: String) {
        this.conversationId = conversationId
    }
}