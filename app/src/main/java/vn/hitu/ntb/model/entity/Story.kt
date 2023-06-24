package vn.hitu.ntb.model.entity

class Story {

    var sId = ""
    var name = ""
    var uId = ""
    var time = ""
    var avatar = ""
    var url = ""
    var type = 0

    var currentItem = 0
    var done = 0


    constructor()

    constructor(sId : String, name : String, uId: String, time : String, avatar: String, url : String, type : Int){
        this.sId = sId
        this.uId = uId
        this.name = name
        this.time = time
        this.avatar = avatar
        this.url = url
        this.type = type
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["sId"] = sId
        result["name"] = name
        result["uId"] = uId
        result["time"] = time
        result["avatar"] = avatar
        result["url"] = url
        result["type"] = type
        return result
    }

}