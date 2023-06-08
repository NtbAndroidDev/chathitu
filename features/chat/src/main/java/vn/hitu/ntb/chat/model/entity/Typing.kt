package vn.hitu.ntb.chat.model.entity

import com.google.gson.annotations.SerializedName

class Typing {

    @SerializedName("user")
    var user: Sender = Sender()

}