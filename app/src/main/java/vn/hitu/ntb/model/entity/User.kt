package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class User {
    //model User node


    @SerializedName("id")
    var id: Int = 0

    var uId = ""

    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("username")
    var username: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("nick_name")
    var nickName: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("birthday")
    var birthday: String = ""

    @SerializedName("address_full_text")
    var addressFullText: String = ""

    @SerializedName("gender")
    var gender: Int = 2

    @SerializedName("auth_type")
    var authType: Int = 0

    @SerializedName("access_token")
    var accessToken: String = ""

    @SerializedName("jwt_token")
    var jwtToken: String = ""

    @SerializedName("refresh_token")
    var refreshToken: String = ""

    @SerializedName("token_type")
    var tokenType: String = ""

    @SerializedName("is_first_time_login")
    var isFirstTimeLogin: Int = 0

    @SerializedName("type")
    var type: String = ""

    @SerializedName("is_allow_advert")
    var isAllowAdvert: Int = 0

    @SerializedName("avatar_three_image")
    var avatarThreeImage: Avatar? = Avatar()

    @SerializedName("restaurantId")
    var restaurantId: Int = 0

    @SerializedName("restaurant_membership_card_name")
    var restaurantMembershipCardName: String = ""

    @SerializedName("alo_point")
    var aloPoint: Int? = 0
    @SerializedName("total_alo_point")
    var totalAloPoint: Int? = 0

    @SerializedName("is_used_biometric")
    var isUsedBiometric: Boolean? = false

    @SerializedName("is_display_nick_name")
    var isDisplayNickName: Int? = 0

    @SerializedName("is_gps")
    var isGPS: Int? = 0

    @SerializedName("lat")
    var lat: String? = ""

    @SerializedName("lng")
    var lng: String? = ""

    @SerializedName("advert_package_for_business_type_id")
    var advertPackageForBusinessTypeId: Int? = 0

    @SerializedName("advert_package_for_premium_type_id")
    var advertPackageForPremiumTypeId: Int? = 0

    @SerializedName("advert_package_status")
    var advertPackageStatus: Int? = 0

    @SerializedName("phone_book_update_time")
    var phoneBookUpdateTime: String? = ""
}
