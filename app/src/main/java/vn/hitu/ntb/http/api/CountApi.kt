package vn.hitu.ntb.http.api

import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/12/2022
 */
class CountApi : BaseApi() {

    var params: Params? = Params()

    class Params {

    }

    companion object {
        fun params(): BaseApi {
            val data = CountApi()
            data.params = Params()
            data.requestUrl = "/api/v1/request-friend/count-tab"
            data.httpMethod = AppConstants.HTTP_METHOD_GET
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}