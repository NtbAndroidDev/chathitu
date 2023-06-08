package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class MessageActionEmit {
    @SerializedName("message_id")
    var messageId: String = ""

    constructor(messageId: String) {
        this.messageId = messageId
    }
}