package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class UserData {
    @SerializedName("uid")
    var uid: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("image")
    var image: String = ""

    @SerializedName("isOnline")
    var isOnline = true

    @SerializedName("did")
    var did: String = ""

    @SerializedName("checked")
    var check = 0
    @SerializedName("is_checked")
    var checked = 0



    var isSwipe = false

    constructor()
    constructor(uid: String, name: String, image: String){
        this.uid = uid
        this.name = name
        this.image = image
    }


    constructor(
        uid: String,
        email: String,
        name: String,
        image: String,
        isOnline: Boolean,
        did: String
    ) {
        this.uid = uid
        this.email = email
        this.name = name
        this.image = image
        this.isOnline = isOnline
        this.did = did
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["uid"] = uid
        result["email"] = email
        result["name"] = name
        result["image"] = image
        result["isOnline"] = isOnline
        result["did"] = did
        return result
    }


    override fun toString(): String {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", isOnline=" + isOnline +
                '}'
    }
}