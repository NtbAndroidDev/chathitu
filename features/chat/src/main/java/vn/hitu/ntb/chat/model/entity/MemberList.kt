package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author:Hồ Quang Tùng
 * @Date: 26/12/2022
 */
class MemberList {

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("contact_type")
    var contactType: Int = 0

    @SerializedName("permission")
    var permission: Int = 0

    @SerializedName("is_tag")
    var isTag: Boolean = false

    @SerializedName("list_permission")
    var listPermission: Int = 0

    var isSelected: Boolean = false
}