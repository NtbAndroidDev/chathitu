package vn.hitu.ntb.model.entity

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 25/05/2023
 */
class LeaderGroup {

    var groupId = ""

    var id = ""


    constructor()

    constructor(groupId : String, id : String){
        this.groupId = groupId
        this.id = id
    }
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["group_id"] = groupId
        result["id"] = id
        return result
    }
}