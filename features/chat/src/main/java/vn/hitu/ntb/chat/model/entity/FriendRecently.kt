package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName
import vn.hitu.ntb.model.entity.AvatarChat
import vn.hitu.ntb.model.entity.Classify
import vn.hitu.ntb.model.entity.LastMessage
/**
 * @Author: NGUYEN THANH BINH
 * @Date: 02/01/2023
 */
class FriendRecently {
    @SerializedName("avatar")
    var avatar: AvatarChat = AvatarChat()

    @SerializedName("classify")
    var classify: Classify = Classify()

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("id")
    var id: String = ""

    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("is_notify")
    var isNotify: Int = 0

    @SerializedName("is_pinned")
    var isPinned: Int = 0

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("last_message")
    var lastMessage: LastMessage = LastMessage()

    @SerializedName("my_permission")
    var myPermission: Int = 0

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("no_of_member")
    var noOfMember: Int = 0

    @SerializedName("no_of_not_seen")
    var noOfNotSeen: Int = 0

    @SerializedName("position")
    var position: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("updated_at")
    var updatedAt: String = ""

    @SerializedName("is_member")
    var member : Int = 0

}