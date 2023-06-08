package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName
import vn.hitu.ntb.model.entity.MediaList
import vn.hitu.ntb.model.entity.Thumbnail
import vn.hitu.ntb.model.entity.UserTag

class Message {
    @SerializedName("message_id")
    var messageId: String = ""

    @SerializedName("conversation_id")
    var conversationId: String = ""

    @SerializedName("type")
    var type: Int = 0

    @SerializedName("message")
    var message: String = ""

    @SerializedName("message_reply_id")
    var messageReplyId: String = ""

    @SerializedName("thumb")
    var thumb: Thumbnail = Thumbnail()

    @SerializedName("created_at")
    var createdAt: String = ""

    @SerializedName("position")
    var position: String = ""

    @SerializedName("user")
    var user: Sender = Sender()

    @SerializedName("tag")
    var tag: ArrayList<UserTag> = ArrayList()

    @SerializedName("is_timeline")
    var isTimeline: Int = 0

    @SerializedName("no_of_reaction")
    var noOfReaction: Int = 0

    @SerializedName("no_of_like")
    var noOfLike: Int = 0

    @SerializedName("no_of_love")
    var noOfLove: Int = 0

    @SerializedName("no_of_haha")
    var noOfHaha: Int = 0

    @SerializedName("no_of_wow")
    var noOfWow: Int = 0

    @SerializedName("no_of_sad")
    var noOfSad: Int = 0

    @SerializedName("no_of_angry")
    var noOfAngry: Int = 0

    @SerializedName("my_reaction")
    var myReaction: Int = 0

    var isStroke: Boolean = false

    @SerializedName("media")
    var media: ArrayList<MediaList> = ArrayList()

    @SerializedName("message_view")
    var messageView: ArrayList<MessageView> = ArrayList()

//    @SerializedName("message_reply")
//    var messageReply: Message = Message()

    @SerializedName("user_target")
    var userTarget: ArrayList<Sender> = ArrayList()

    @SerializedName("sticker")
    var sticker: MediaList = MediaList()
}