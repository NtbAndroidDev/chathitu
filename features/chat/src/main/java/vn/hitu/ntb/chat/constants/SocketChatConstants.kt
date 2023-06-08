package vn.hitu.ntb.chat.constants
/**
 * @AuthorUpdate: HO QUANG TUNG
 * @Date: 02/01/2023
 */
object SocketChatConstants {
    const val ON_UPDATE_PERMISSION = "listen-update-permission-conversation"
    const val EMIT_UPDATE_PERMISSION = "update-permission-conversation"
    const val JOIN_ROOM = "join-room"
    const val LEAVE_ROOM = "leave-room"

    const val ON_ADD_MEMBER = "listen-add-member-conversation"
    const val EMIT_ADD_MEMBER = "add-member-conversation"

    //change name group
    const val EMIT_CHANGE_NAME_GROUP = "update-name-conversation"
    const val ON_CHANGE_NAME_GROUP = "listen-update-name-conversation"

    //change avatar group
    const val EMIT_CHANGE_AVATAR_GROUP = "update-avatar-conversation"
    const val ON_CHANGE_AVATAR_GROUP = "listen-update-avatar-conversation"


    const val ON_DISBAND_GROUP = "listen-disband-conversation"
    const val EMIT_DISBAND_GROUP = "disband-conversation"

    const val ON_OUT_GROUP = "listen-out-conversation"
    const val EMIT_OUT_GROUP = "out-conversation"

    const val ON_REMOVE_MEMBER = "listen-remove-member-conversation"
    const val EMIT_REMOVE_MEMBER = "remove-member-conversation"

    //typing on
    const val ON_TYPING_ON = "listen-typing-on"
    const val EMIT_TYPING_ON = "typing-on"

    //typing off
    const val ON_TYPING_OFF = "listen-typing-off"
    const val EMIT_TYPING_OFF = "typing-off"
    //Chat text
    const val EMIT_CHAT_TEXT = "message-text"
    const val ON_CHAT_TEXT = "listen-message-text"

    //Chat image
    const val EMIT_CHAT_IMAGE = "message-image"
    const val ON_CHAT_IMAGE = "listen-message-image"

    // Chat video
    const val EMIT_CHAT_VIDEO = "message-video"
    const val ON_CHAT_VIDEO = "listen-message-video"

    //Thu hồi
    const val EMIT_REVOKE = "message-revoke"
    const val ON_REVOKE = "listen-message-revoke"

    //Trả lời
    const val EMIT_REPLY = "message-reply"
    const val ON_REPLY = "listen-message-reply"

    //Chat sticker
    const val EMIT_STICKER = "message-sticker"
    const val ON_STICKER = "listen-message-sticker"

    //socket nhận group mới
    const val LISTEN_NEW_CONVERSATION = "listen-new-conversation"

    //Reaction tin nhắn
    const val EMIT_REACTION_MESSAGE = "reaction-message"
    const val ON_REACTION_MESSAGE = "listen-reaction-message"

    //Thu hồi reaction tin nhắn
    const val EMIT_REMOVE_REACTION_MESSAGE = "remove-reaction-message"
    const val ON_REMOVE_REACTION_MESSAGE = "listen-remove-reaction-message"
}