package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * Author: Nguyễn Khánh Duy
 * Date : 09/01/2022
 */

class SearchUserApi : BaseApi() {
    var params: Params? = Params()

    class Params {
        @HttpRename("phone")
        var phone: String? = ""
    }

    companion object {
        fun params(phone: String): BaseApi {
            val data = SearchUserApi()
            data.requestUrl = "/api/v1/user/phone"
            data.params = Params()
            data.params!!.phone = phone
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = BuildConfig.SERVER_USER
            data.authorization = UserCache.getNodeToken()
            return data
        }
    }
}