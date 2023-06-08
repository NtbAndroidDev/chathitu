package vn.hitu.ntb.chat.constants

/**
 * @Author: Phạm Văn Nhân
 * @Date: 05/10/2022
 */
class MessageTypeChatConstants {
    companion object {
        const val EMPTY = 0
        const val TEXT = 1
        const val IMAGE = 2
        const val VIDEO = 3
        const val AUDIO = 4
        const val FILE = 5
        const val REPLY = 6
        const val UPDATE_NAME = 7
        const val UPDATE_AVATAR = 8
        const val UPDATE_BACKGROUND = 9
        const val REMOVE_USER = 10
        const val ADD_NEW_USER = 11
        const val CHANGE_PERMISSION_USER = 12 // (tin nhắn cho mọi người trong room khi có người được đổi quyền thành trưởng, phó nhóm)
        const val USER_OUT_GROUP = 13
        const val REVOKE_MESSAGE = 14 //(tin nhắn thu hồi)
        const val STICKER = 15
        const val NEW_GROUP = 16 //(tin nhắn của nhóm mới được tạo)
        const val UPDATE_PROFILE = 18
        const val PINNED = 19
        const val CREATE_REMINDER = 20
        const val CREATE_VOTE = 21
        const val CONFIRM_NEW_MEMBER = 22

        //Chưa có type này
        const val CONTACT = 100
    }
}