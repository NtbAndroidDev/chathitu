package vn.hitu.ntb.model.entity

import com.google.firebase.database.Exclude

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class ChatMessage {
    var messageTime: String = ""

    var message: String = ""
    var sendBy: String = ""
    var typeMessage: String = ""
    var messageId: String = ""

    var stop: Boolean = false
    var seekTo = 0
    var play = false


    constructor(
        messageTime: String,
        message: String,
        sendBy: String,
        typeMessage: String,
        messageId: String
    ) {
        this.messageTime = messageTime
        this.message = message
        this.sendBy = sendBy
        this.typeMessage = typeMessage
        this.messageId = messageId
    }


    constructor()

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["messageTime"] = messageTime
        result["message"] = message
        result["sendBy"] = sendBy
        result["typeMessage"] = typeMessage
        result["messageId"] = messageId
        return result
    }
}