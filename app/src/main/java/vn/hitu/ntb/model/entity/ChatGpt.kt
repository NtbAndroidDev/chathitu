package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class ChatGpt {

    @SerializedName("message")
    var message = ""

    @SerializedName("type")
    var type = ""


    constructor()

    constructor(content : String){
        this.message = content
        this.type = "i"
    }
}