package vn.hitu.ntb.constants

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class AppConstants {
    companion object {
        //Config call api
        const val APP_OS_NAME = "android"
        const val HTTP_METHOD_POST = 1
        const val HTTP_METHOD_GET = 0

        const val METHOD_POST = "POST"
        const val METHOD_GET = "GET"

        const val CACHE_USER = "CACHE_USER"
        const val PUSH_TOKEN = "PUSH_TOKEN"
        const val CACHE_POINT_BONUS_LEVEL = "CACHE_POINT_BONUS_LEVEL"
        const val CACHE_IMAGE_DETECT = "CACHE_IMAGE_DETECT"
        const val CACHE_CONFIG = "CACHE_CONFIG"
        const val CACHE_UPLOAD_TYPE_KEY = "CACHE_UPLOAD_TYPE_KEY"
        const val CACHE_LOCATION = "CACHE_LOCATION"
        const val USING_BIOMETRIC = "USING_BIOMETRIC"


        const val POSITION_MEDIA = "POSITION_MEDIA"
        const val MEDIA_COUNT_VISIBLE = "MEDIA_COUNT_VISIBLE"
        const val DATA_MEDIA = "DATA_MEDIA"




        //

        const val ID_USER = "ID_USER"
        const val VIDEO_YOUTUBE = "VIDEO_YOUTUBE"

        const val ITS_ME = 0 // chính mình
        const val NOT_FRIEND = 1 // người khác
        const val WAITING_RESPONSE = 3 // mình gửi lời mời, đợi họ phản hồi
        const val FRIEND = 4


        const val INFO_CUSTOMER = "INFO_CUSTOMER"




        //Type chi tiết bài
        const val KEY_DETAIL_TIMELINE = "KEY_DETAIL_TIMELINE"



        //Upload type key
        const val NEWSFEED_KEY = 1
        const val NEWSFEED_KEY_REVIEW = 3
        const val COMMENT_KEY = 2
        const val UPLOAD_AVATAR = 4
        const val REGISTER_AVATAR = "REGISTER_AVATAR"

        const val FRAGMENT_PROFILE = 4
        const val FRAGMENT_MESSAGE = 3

        const val CODE_REGISTER_MEMBER = "TECHRES"
        const val LINK_GROUP = "aloline.vn/u/"
        const val TYPE_COMMENT = "TYPE_COMMENT"

        const val NEWSFEED_TYPE_KEY = "NEWSFEED_TYPE_KEY"
        const val BOOKING_NOTIFY_TYPE_KEY = "BOOKING_NOTIFY_TYPE_KEY"
        const val ORDER_NOTIFY_TYPE_KEY = "ORDER_NOTIFY_TYPE_KEY"
        const val NEWSFEED_USER = 0
        const val NEWSFEED_RESTAURANT = 1
        const val BOOKING_NOTIFICATION = 1
        const val ORDER_NOTIFICATION = 1



        const val TYPE_PRIVATE = 2 // tin nhắn bạn bè
        const val GROUP_TYPE = "GROUP_TYPE"
        const val NAME_GROUP = "NAME_GROUP"  //tên group
        const val ID_GROUP = "ID_GROUP"  // id group
        const val AVT_GROUP = "AVT_GROUP"  //avt group
        const val NO_OF_MEMBER = "NO_OF_MEMBER"  // thành viên trong group
        const val FOLDER_APP = "hitu"
        const val GROUP_DATA = "GROUP_DATA"


        const val NEW_MESSAGE = "last-message"
        const val CUSTOMER_ORDERS = "customer_orders"


        //notification
        const val DETAIL_NEWS_FEED_ID_NOTIFICATION = "DETAIL_NEWS_FEED_ID_NOTIFICATION"
        const val CUSTOMER_ID_NOTIFICATION = "CUSTOMER_ID_NOTIFICATION"
        const val BOOKING_ID_NOTIFICATION = "BOOKING_ID_NOTIFICATION"
        const val ORDER_ID_NOTIFICATION = "ORDER_ID_NOTIFICATION"
        const val GIFT_ID_NOTIFICATION = "GIFT_ID_NOTIFICATION"
        const val MEMBERSHIP_CARD_ID_NOTIFICATION = "MEMBERSHIP_CARD_ID_NOTIFICATION"
        const val LAT = "LAT"
        const val LNG = "LNG"
        const val OBJECT_ID = "OBJECT_ID"
        const val KEY_ID_ORDER_CUSTOMER = "KEY_ID_ORDER_CUSTOMER"
        const val OBJECT_TYPE = "OBJECT_TYPE"
        const val UUID = "UUID"
        const val UPLOAD_IMAGE = "http://172.16.10.85:9007/api/v1/media/upload"
        const val API_ORDER = "http://172.16.2.240:1483"
        const val PROGRESS = "progress"

    }

}