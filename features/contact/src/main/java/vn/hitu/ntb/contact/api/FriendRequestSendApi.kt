package vn.hitu.ntb.contact.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.http.api.BaseApi

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/2/22
 * Api lấy danh sách lời mời đã gửi
 */
class FriendRequestSendApi : BaseApi(){

    var params: Params? = Params()

    class Params {
        @HttpRename("id")
        var id: Int = UserCache.getUser().id

        @HttpRename("limit")
        var limit: Int = 20

        @HttpRename("position")
        var position: String = ""
    }

    companion object {
        fun params(position : String): BaseApi {
            val data = FriendRequestSendApi()
            data.requestUrl = "/api/v1/request-friend/send"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_GET
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            data.params!!.position = position
            return data
        }
    }
}