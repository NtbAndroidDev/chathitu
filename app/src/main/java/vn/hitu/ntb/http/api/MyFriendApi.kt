package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants

/**
 * @Author: Phạm Văn Nhân
 * @Date: 28/09/2022
 * @author: NGUYEN THANH BINH
 * @Date: 01/12/2022
 */
class MyFriendApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("id")
        var id: Int = UserCache.getUser().id

        @HttpRename("limit")
        var limit: Int = 20

        @HttpRename("position")
        var position: String = ""

        @HttpRename("key_search")
        var keySearch: String = ""
    }

    companion object {
        fun params(idUser : Int, position : String, keySearch : String): BaseApi {
            val data = MyFriendApi()
            data.requestUrl = "/api/v1/friend/$idUser"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_GET
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            data.params!!.position = position
            data.params!!.keySearch = keySearch
            return data
        }
    }
}