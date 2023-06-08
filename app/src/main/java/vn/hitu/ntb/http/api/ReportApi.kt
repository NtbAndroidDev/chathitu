package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
/**
 * @Update: NGUYEN THANH BINH
 * @Date: 01/12/2022
 */
class ReportApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("report_user_id")
        var reportUserId: Int = 0

        @HttpRename("type_report")
        var typeReport: Int = 0

        @HttpRename("content_id")
        var contentId: String = "0"

        @HttpRename("content_report")
        var contentReport: String = ""

        @HttpRename("type")
        var type: Int = 1
    }

    companion object {
        fun params(
            contentId: String,
            reportUserId: Int,
            typeReport: Int,
            contentReport: String,
            type: Int
        ): BaseApi {
            val data = ReportApi()
            data.requestUrl = "/api/v1/report/create"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = BuildConfig.SERVER_REPORT
            data.authorization = UserCache.getNodeToken()
            data.params!!.contentId = contentId
            data.params!!.reportUserId = reportUserId
            data.params!!.typeReport = typeReport
            data.params!!.contentReport = contentReport
            data.params!!.type = type

            return data
        }
    }
}