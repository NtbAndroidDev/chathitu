package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: HỒ QUANG TÙNG
 * @Date: 4/1/2022
 */
class NotificationApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("position")
        var position: String? = ""

        @HttpRename("limit")
        var limit: Int? = 0
    }

    companion object {
        fun params(position: String, limit: Int): BaseApi {
            val data = NotificationApi()
            data.requestUrl = "/api/v1/logs"
            data.params = Params()
            data.params!!.limit = limit
            data.params!!.position = position
            data.httpMethod = AppConstants.HTTP_METHOD_GET
            data.projectId = BuildConfig.SERVER_LOG_NOTIFICATION
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}