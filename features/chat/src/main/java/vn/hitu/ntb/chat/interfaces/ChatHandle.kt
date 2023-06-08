package vn.hitu.ntb.chat.interfaces

import android.view.View
import vn.hitu.ntb.chat.model.entity.MemberList
import vn.hitu.ntb.chat.model.entity.Message
import vn.hitu.ntb.chat.model.entity.ReactionItem

interface ChatHandle {
    fun clickTagMember(member: MemberList)
    fun onRevoke(messagesByGroup: Message, view: View, y: Int)
    fun onRevokeClick(messagesByGroup: Message, view: View?, view1: View?)
    fun onRevokeEmoji(
        messagesByGroup: Message,
        view: View?,
        reactionItems: List<ReactionItem?>?,
        y: Int
    )
    fun clickStickerFromFirstMessage(
        id : String
    )
}