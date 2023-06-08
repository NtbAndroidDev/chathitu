package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 28/12/2022
 */

class GroupSocket {
    @SerializedName("type")
    var type: Int = 0

    @SerializedName("is_pinned")
    var isPinned: Int = 0

    @SerializedName("no_of_member")
    var noOfMember: Int = 0

    @SerializedName("id")
    var id: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("avatar")
    var avatar: AvatarChat = AvatarChat()

    @SerializedName("postion")
    var postion: String = ""

    @SerializedName("last_message")
    var lastMessage: String = ""

    @SerializedName("is_notify")
    var isNotify: Int = 0

    @SerializedName("no_of_not_seen")
    var noOfNotSeen: Int = 0

    @SerializedName("my_permmission")
    var myPermission: Int = 0

    @SerializedName("classify")
    var classify: Classify = Classify()

    @SerializedName("last_activity")
    var lastActivity: String = ""

    @SerializedName("updated_at")
    var updatedAt: String = ""
}