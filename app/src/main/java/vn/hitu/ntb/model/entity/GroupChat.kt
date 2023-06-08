package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Phạm Văn Nhân
 * @Date: 03/10/2022
 */
class GroupChat {
    @SerializedName("name")
    var name: String = ""

    @SerializedName("id")
    var id: String = ""

    @SerializedName("position")
    var position: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("last_message")
    var lastMessage: LastMessage = LastMessage()

    @SerializedName("is_pinned")
    var isPinned: Int = 0

    @SerializedName("is_notify")
    var isNotify: Int = 0

    @SerializedName("is_hidden")
    var isHidden: Int = 0

    @SerializedName("avatar")
    var avatar: AvatarChat = AvatarChat()

    @SerializedName("no_of_member")
    var noOfMember: Int = 0

    @SerializedName("no_of_not_seen")
    var noOfNotSeen: Int = 0

    @SerializedName("my_permission")
    var myPermission: Int = 0

    @SerializedName("classify")
    var classify: Classify = Classify()

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("updated_at")
    var updatedAt: String = ""

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("link_join")
    var linkJoin: String = ""

}