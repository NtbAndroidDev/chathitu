package vn.hitu.ntb.http.api

import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 01/12/2022
 */
class NotAcceptFriendApi : BaseApi() {

    var params: Params? = Params()

    class Params

    companion object {
        fun params(id: Int): BaseApi {
            val data = NotAcceptFriendApi()
            data.requestUrl = "/api/v1/request-friend/$id/denied"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}