package vn.hitu.ntb.constants

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 */
class ModuleClassConstants {
    companion object {
        //MODULE Đăng nhập
        const val LOGIN_ACTIVITY = "vn.hitu.ntb.login.ui.activity.LoginActivity"
        const val CHAT_MESSAGE_ACTIVITY = "vn.hitu.ntb.chat.ui.activity.ChatActivity"


        //chọn nhà hàng  kho bia
        const val CHOOSE_BRAND = "vn.hitu.ntb.restaurant.ui.dialog.DialogChooseRestaurant"

        //tất cả nhà hàng theo loại
        const val ALL_RESTAURANT_BY_TYPE =
            "vn.hitu.ntb.restaurant.ui.activity.AllRestaurantBySortActivity"

        //        Quản lý đơn hàng
        const val ORDER_MANAGER =
            "vn.hitu.ntb.order_management.ui.activity.OrderManagerActivity"
        const val DETAIL_ORDER =
            "vn.hitu.ntb.order_management.ui.activity.DetailOrderActivity"

        //Chi tiết VAT
        const val VAT_DETAIL = "vn.hitu.ntb.order_management.ui.activity.VATDetailActivity"
        const val RESTAURANT_ACTIVITY =
            "vn.hitu.ntb.restaurant.ui.activity.MyRestaurantActivity"
        const val SCAN_QR_CODE_ACTIVITY = "vn.hitu.ntb.qr_code.ui.activity.QrManagerActivity"

        // màn hình thông tin chi tiết nhà hàng
        const val DETAIL_BRAND_ACTIVITY =
            "vn.hitu.ntb.restaurant.ui.activity.DetailBrandActivity"
        const val BRANCH_ACTIVITY =
            "vn.hitu.ntb.restaurant.ui.activity.DetailBranchActivity"


        //MODULE QUÀ TẶNG
        const val GIFT_ACTIVITY = "vn.hitu.ntb.my_gift.ui.activity.MyGiftActivity"


        //MODULE ĐẶT BÀN (BOOKING)
        const val BOOKING_ACTIVITY = "vn.hitu.ntb.booking.ui.activity.BookingManagerActivity"


        //MODULE CHUỖI CHI NHÁNH NHÀ HÀNG
        const val CHOOSE_TRADEMARK_RESTAURANT_ACTIVITY =
            "vn.hitu.ntb.choose_branch_restaurant.ui.activity.ChooseRestaurantActivity"

        //MODULE QUẢN LÝ QUẢNG CÁO
        const val ADVERTISEMENT_ACTIVITY =
            "vn.hitu.ntb.commercial.ui.activity.FormRegistryAdvertActivity"

        //MODULE DANH BẠ
        // const val CONTACT_FRAGMENT = "vn.hitu.ntb.contact.ui.fragment.ContactFragment"
        const val REQUEST_FRIEND = "vn.hitu.ntb.contact.ui.activity.RequestFriendActivity"
        const val PHONE_BOOK_ACTIVITY = "vn.hitu.ntb.contact.ui.activity.PhoneBookActivity"

        //MODULE NEWS FEED
        const val TIME_LINE_FRAGMENT =
            "vn.hitu.ntb.news_feed.ui.fragment.TimeLineFragment"

        const val NEWS_FEED_REVIEW =
            "vn.hitu.ntb.news_feed.ui.activity.CreateReviewNewsFeedActivity"
        const val NEWS_FEED_POST =
            "vn.hitu.ntb.news_feed.ui.activity.CreatePostNewsFeedActivity"
        const val DETAIL_TIMELINE =
            "vn.hitu.ntb.news_feed.ui.activity.DetailTimeLineActivity"
        const val DETAIL_BOOKING =
            "vn.hitu.ntb.booking.ui.activity.DetailBookingActivity"

        //MÀN HÌNH THÔNG TIN ĐIỂM
        const val POINT_INFORMATION =
            "vn.hitu.ntb.point_information.ui.activity.PointInformationActivity"

        //MODULE CHAT
        const val CHAT = "vn.hitu.ntb.chat.ui.fragment.ChatManagerFragment"
        const val CALL_HISTORY = "vn.hitu.ntb.chat.ui.fragment.CallHistoryFragment"
        const val CREATE_GROUP_CHAT = "vn.hitu.ntb.chat.ui.activity.CreateGroupChatActivity"
        const val CHAT_ACTIVITY = "vn.hitu.ntb.chat.ui.activity.ChatActivity"
        const val QR_GROUP = "vn.hitu.ntb.chat.ui.activity.QrGroupActivity"

        //MODULE HOME
        const val HOME = "vn.hitu.ntb.home.ui.fragment.HomeFragment"
        const val ACCOUNT = "vn.hitu.ntb.ui.fragment.AccountFragment"

        //MODULE danh bạ
        const val CONTACT = "vn.hitu.ntb.contact.ui.fragment.ContactFragment"

        const val CHAT_MANAGER = "vn.hitu.ntb.ui.activity.HomeActivity"

        //MODULE CHỈNH SỬA THÔNG TIN CÁ NHÂN
        const val EDIT_PROFILE = "vn.hitu.ntb.edit_profile.ui.activity.EditProfileActivity"
        const val DETAIL_PROFILE =
            "vn.hitu.ntb.info_customer.ui.activity.DetailCustomerActivity"
        const val INFO_CUSTOMER = "vn.hitu.ntb.info_customer.ui.activity.InfoCustomerActivity"
        const val ALL_MY_FRIEND = "vn.hitu.ntb.ui.activity.AllMyFriendActivity"
        const val ALL_SUGGEST_FRIEND =
            "vn.hitu.ntb.news_feed.ui.activity.AllSuggestFriendActivity"
        const val COMMERCIAL = "vn.hitu.ntb.commercial.ui.activity.AdvertisementActivity"
        const val FROM_REGISTER_ADVERT_ACTIVITY =
            "vn.hitu.ntb.commercial.ui.activity.FormRegistryAdvertActivity"
        const val ADVERTISEMENT_SUCCESS_ACTIVITY =
            "vn.hitu.ntb.commercial.ui.activity.AdvertisementRegistrySuccessActivity"
        const val ADVERTISEMENT_PACKAGE_ACTIVITY =
            "vn.hitu.ntb.ui.activity.AdvertPackageActivity"

        const val MEDIA_SLIDER_ACTIVITY =
            "vn.hitu.ntb.media_slider.ui.activity.MediaSliderActivity"

        // Draft bản nháp
        const val DRAFT =
            "vn.hitu.ntb.news_feed.ui.activity.DraftActivity"

        const val CREATE_POST_NEWS_FEED =
            "vn.hitu.ntb.news_feed.ui.activity.CreatePostNewsFeedActivity"

        //        Chat
        const val SETTING_BROWSE_MEMBER =
            "vn.hitu.ntb.chat.ui.activity.SettingBrowseMemberActivity"

        const val SETTING_CHAT =
            "vn.hitu.ntb.chat.ui.activity.SettingChatActivity"

        const val DETAIL_CHAT =
            "vn.hitu.ntb.chat.ui.activity.DetailChatActivity"
        const val TRANSFER_FER_RIGHT =
            "vn.hitu.ntb.chat.ui.activity.TransferRightsActivity"

        const val ADD_MEMBER_GROUP =
            "vn.hitu.ntb.chat.ui.activity.AddMemberGroupActivity"

        const val LIST_MEMBER_GROUP =
            "vn.hitu.ntb.chat.ui.activity.ListMemberActivity"

        const val SETTING_USER =
            "vn.hitu.ntb.chat.ui.activity.SettingUserActivity"

        const val ADD_DEPUTY =
            "vn.hitu.ntb.chat.ui.activity.AddDeputyActivity"


        const val ADD_GROUP = "vn.hitu.ntb.chat.ui.activity.AddGroupActivity"
    }
}