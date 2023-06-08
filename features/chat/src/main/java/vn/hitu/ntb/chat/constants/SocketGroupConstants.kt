package vn.hitu.ntb.chat.constants

/**
 * @Author: Phạm Văn Nhân
 * @Date: 06/10/2022
 */
class SocketGroupConstants {
    companion object {
        //Res
        const val NEW_MESSAGE = "last-message"

        const val LISTEN_NEW_CONVERSATION = "listen-new-conversation" //socket nhận group mới

        const val MESSAGE_NOT_SEEN_BY_ONE_GROUP = "message-not-seen-by-one-group"
        const val MESSAGE_NOT_SEEN_BY_ONE_GROUP_PERSONAL = "message-not-seen-by-one-group-personal"
        const val RES_REMOVE_USER_OUT_ROOM_ALOLINE = "res-remove-user-out-room-aloline"
        const val RES_REMOVE_GROUP_ALO_LINE = "res-remove-group-aloline"
    }
}