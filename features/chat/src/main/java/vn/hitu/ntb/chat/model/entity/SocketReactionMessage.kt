package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class SocketReactionMessage {
    @SerializedName("type")
    var type = 0

    @SerializedName("message_id")
    var messageId = ""

    @SerializedName("total_reaction")
    var totalReaction = 0

    @SerializedName("no_of_angry")
    var noOfAngry = 0

    @SerializedName("no_of_haha")
    var noOfHaha = 0

    @SerializedName("no_of_like")
    var noOfLike = 0

    @SerializedName("no_of_love")
    var noOfLove = 0

    @SerializedName("no_of_sad")
    var noOfSad = 0

    @SerializedName("no_of_wow")
    var noOfWow = 0

    @SerializedName("user")
    var user = Sender()
}