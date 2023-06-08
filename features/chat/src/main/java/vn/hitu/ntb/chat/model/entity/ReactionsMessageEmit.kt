package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class ReactionsMessageEmit {
    @SerializedName("type")
    var type = 0

    @SerializedName("message_id")
    var messageId = ""

    @SerializedName("conversation_id")
    var conversationId = ""

    constructor(type: Int, messageId: String, conversationId: String) {
        this.type = type
        this.messageId = messageId
        this.conversationId = conversationId
    }
}