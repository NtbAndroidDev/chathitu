package vn.hitu.ntb.qr_code.api

import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.http.api.BaseApi

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/1/22
 */
class RequestFriendQRApi : BaseApi(){
    var params: Params? = Params()

    class Params {

    }

    companion object {
        fun params(id : Int): BaseApi {
            val data = RequestFriendQRApi()
            data.requestUrl = "/api/v1/request-friend/$id/qr-code"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = BuildConfig.SERVER_FRIEND
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}