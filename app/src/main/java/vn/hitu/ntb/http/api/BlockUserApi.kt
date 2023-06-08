package vn.hitu.ntb.http.api

import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/1/22
 */
class BlockUserApi : BaseApi() {
    var params: Params? = Params()

    class Params

    companion object {
        fun params(UserId: Int): BaseApi {
            val data = BlockUserApi()
            data.requestUrl = "/api/v1/block-user/$UserId"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}