package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: Phạm Văn Nhân
 * @Date: 03/10/2022
 * @Update: NGUYEN THANH BINH
 */
class CreateConversationApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("member_id")
        var memberId: Int = 0
    }

    companion object {
        fun params(memberId : Int): BaseApi {
            val data = CreateConversationApi()
            data.requestUrl = "/api/v1/conversation/create"
            data.projectId = 7012
            data.authorization = UserCache.getNodeToken()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.params!!.memberId = memberId
            return data
        }
    }
}