package vn.hitu.ntb.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @Author: Phạm Văn Nhân
 * @Date: 04/10/2022
 *@Update: NGUYEN THANH BINH
 */
@Entity(tableName = "contact")
class Friend {

    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    var userId: Int = 0

    @SerializedName("status")
    @ColumnInfo(name = "status")
    var status: Int? = 0

    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: Int? = 0

    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String? = ""

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String? = ""

    @SerializedName("nick_name")
    @ColumnInfo(name = "nick_name")
    var nickName: String? = ""


    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    var phone: String? = ""

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar")
    var avatar: String = ""

    @SerializedName("gender")
    @ColumnInfo(name = "gender")
    var gender: Int = -1

    @SerializedName("no_of_follow")
    @ColumnInfo(name = "no_of_follow")
    var noOfFollow: Int = -1

    @SerializedName("mutual_friend")
    @ColumnInfo(name = "mutual_friend")
    var mutualFriend: Int = 0

    @SerializedName("contact_type")
    @ColumnInfo(name = "contact_type")
    var contactType: Int = -1

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    var timestamp: String = ""

    @SerializedName("position")
    @ColumnInfo(name = "position")
    var position: String = ""

    @ColumnInfo(name = "is_new")
    @SerializedName("is_new")
    var isNew = false

    @SerializedName("permission")
    var permission: Int = 0

    @ColumnInfo(name = "is_checked")
    @SerializedName("is_checked")
    var checked = 0

    @SerializedName("checked")
    var check = 0

    @SerializedName("is_member")
    var member: Int = 0

    var isSelected: Boolean = false
    var lastActivity: String = ""


}