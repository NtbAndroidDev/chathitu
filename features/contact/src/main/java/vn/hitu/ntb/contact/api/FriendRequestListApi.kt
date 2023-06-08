package vn.hitu.ntb.contact.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.http.api.BaseApi

/**
 * @Author: Phạm Văn Nhân
 * @Date: 04/10/2022
 * @Update: NGUYEN THANH BINH
 * @Date: 01/12/2022
 * API lây danh sách lời mời kểt bạn
 */
class FriendRequestListApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("id")
        var id: Int = UserCache.getUser().id

        @HttpRename("limit")
        var limit: Int = 49

        @HttpRename("position")
        var position: String = ""
    }

    companion object {
        fun params(position : String, limit : Int): BaseApi {
            val data = FriendRequestListApi()
            data.requestUrl = "/api/v1/request-friend/list"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_GET
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            data.params!!.position = position
            data.params!!.limit = limit
            return data
        }
    }
}