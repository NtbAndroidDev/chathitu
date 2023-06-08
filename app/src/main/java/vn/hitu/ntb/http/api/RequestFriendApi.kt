package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/1/22
 */
class RequestFriendApi : BaseApi(){
    var params: Params? = Params()

    class Params {
        @HttpRename("id")
        var id : Int? = 0
    }

    companion object {
        fun params(id : Int): BaseApi {
            val data = RequestFriendApi()
            data.requestUrl = "/api/v1/request-friend/$id"
            data.params = Params()
            data.params!!.id = id
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = BuildConfig.SERVER_FRIEND
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}