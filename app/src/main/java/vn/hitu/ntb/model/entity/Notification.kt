package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: HO QUANG TUNG
 * @Date: 02/01/2023
 */
class Notification {
    @SerializedName("id")
    var id: String? = ""

    @SerializedName("avatar")
    var avatar: String? = ""

    @SerializedName("image_url")
    var imageUrl: String? = ""

    @SerializedName("title")
    var title: String? = ""

    @SerializedName("content")
    var content: String? = ""

    @SerializedName("object_id")
    var objectId: String? = ""

    @SerializedName("object_type")
    var objectType: Int? = 0

    @SerializedName("notification_type")
    var notificationType: Int? = 0

    @SerializedName("json_addition_data")
    var jsonAdditionData: String? = ""

    @SerializedName("type")
    var type: Int? = 0

    @SerializedName("landing_page_url")
    var landingPageUrl: String? = ""

    @SerializedName("is_viewed")
    var isViewed: Int? = 0

    @SerializedName("timestamp")
    var timestamp: String? = ""

    @SerializedName("position")
    var position: String? = ""
    @SerializedName("reaction_type")
    var reactionType: Int? = 0


}