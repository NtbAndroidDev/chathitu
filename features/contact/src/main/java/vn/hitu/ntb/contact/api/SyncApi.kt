package vn.hitu.ntb.contact.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.ContactDevice
import vn.hitu.ntb.http.api.BaseApi
/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/2/22
 * Api đồng bộ danh bạ
 */
class SyncApi : BaseApi() {

    var params: Params? = Params()

    class Params {
        @HttpRename("contact")
        var contact: ArrayList<ContactDevice> = ArrayList()

    }

    companion object {
        fun params(contact : ArrayList<ContactDevice>): BaseApi {
            val data = SyncApi()
            data.requestUrl = "/api/v1/friend/sync"
            data.params = Params()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            data.projectId = 7001
            data.authorization = UserCache.getNodeToken()
            data.params!!.contact = contact
            return data
        }
    }
}