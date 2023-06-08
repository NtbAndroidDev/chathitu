package vn.hitu.ntb.model.entity

class GroupData {
    var gid: String = ""
    var name: String = ""
    var listUidMember: ArrayList<String> = ArrayList()
    var imageId: String = ""
    var isOnline = false
    var lastMessage: String = ""
    var lastTime: String = ""
    var background: String = ""

    constructor()

    constructor(
        gid: String,
        name: String,
        listUidMember: ArrayList<String>,
        imageId: String,
        isOnline: Boolean,
        lastMessage: String,
        lastTime: String,
        background: String
    ) {
        this.gid = gid
        this.name = name
        this.listUidMember = listUidMember
        this.imageId = imageId
        this.isOnline = isOnline
        this.lastMessage = lastMessage
        this.lastTime = lastTime
        this.background = background
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["gid"] = gid
        result["name"] = name
        result["listUidMember"] = listUidMember
        result["imageId"] = imageId
        result["isOnline"] = isOnline
        result["lastMessage"] = lastMessage
        result["lastTime"] = lastTime
        result["background"] = background
        return result
    }

    override fun toString(): String {
        return "Group{" +
                "gid='" + gid + '\'' +
                ", name='" + name + '\'' +
                ", listUidMember=" + listUidMember +
                ", imageId='" + imageId + '\'' +
                ", isOnline=" + isOnline +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastTime='" + lastTime + '\'' +
                '}'
    }


}