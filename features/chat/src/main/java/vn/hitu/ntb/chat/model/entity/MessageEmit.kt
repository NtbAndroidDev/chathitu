package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName
import vn.hitu.ntb.model.entity.Thumbnail
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 28/12/2022
 */
class MessageEmit {
    @SerializedName("message")
    var message = ""

    @SerializedName("tag")
    var tag = ArrayList<Tag>()

    @SerializedName("thumb")
    var thumb = Thumbnail()

    @SerializedName("link")
    var link = ArrayList<String>()

    @SerializedName("media")
    var media = ArrayList<String>()

    @SerializedName("sticker_id")
    var stickerId = ""

    @SerializedName("message_reply_id")
    var messageReplyId = ""

    @SerializedName("key_error")
    var keyError = AppUtils.getRandomString(12)



    constructor(message: String) {
        this.message = message
    }

    constructor(message: String, tag: ArrayList<Tag>) {
        this.message = message
        this.tag = tag
    }

    constructor(
        message: String,
        tag: ArrayList<Tag>,
        thumb: Thumbnail,
        link: ArrayList<String>
    ) {
        this.message = message
        this.tag = tag
        this.thumb = thumb
        this.link = link
    }

    constructor(media: ArrayList<String>) {
        this.media = media
    }

    constructor()
}