package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class ConfigApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("project_id")
        var projectId: String? = BuildConfig.PROJECT_ID
    }

    companion object {
        fun params(): BaseApi {
            val data = ConfigApi()
            data.requestUrl = "api/configs"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_GET
            data.projectId = BuildConfig.SERVER_OAUTH
            return data
        }
    }
}