package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class ProfileCustomerNodeData {
    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("is_online")
    var isOnline: Int = 0

    @SerializedName("nick_name")
    var nickName: String = ""

    @SerializedName("avatar")
    var avatar: String = ""

    @SerializedName("full_name")
    var fullName: String = ""

    @SerializedName("identification")
    var identification: Int = 0

    @SerializedName("address")
    var address: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("birthday")
    var birthday: String = ""

    @SerializedName("no_of_friend")
    var noOfFriend: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("gender")
    var gender: Int = 0

    @SerializedName("city")
    var city: City = City()

    @SerializedName("district")
    var district: District = District()

    @SerializedName("ward")
    var ward: Ward = Ward()

    @SerializedName("country")
    var country: Country = Country()

    @SerializedName("contact_type")
    var contactType: Int = 0

    @SerializedName("is_enable_birthday")
    var isEnableBirthday: Int = 0

    @SerializedName("is_enable_phone")
    var isEnablePhone: Int = 0

    @SerializedName("is_enable_email")
    var isEnableEmail: Int = 0

    @SerializedName("is_enable_address")
    var isEnableAddress: Int = 0

    @SerializedName("is_enable_city")
    var isEnableCity: Int = 0

    @SerializedName("is_enable_district")
    var isEnableDistrict: Int = 0

    @SerializedName("is_enable_ward")
    var isEnableWard: Int = 0

    @SerializedName("is_display_name")
    var isDisplayNickName: Int = 0

    @SerializedName("is_enable_gender")
    var isEnableGender: Int = 0

    @SerializedName("mutual_friend")
    var mutualFriend: Int = 0

    @SerializedName("no_of_alo_point")
    var noOfAloPoint: Int = 0


}
