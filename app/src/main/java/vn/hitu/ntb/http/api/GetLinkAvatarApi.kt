package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.model.entity.Medias

/**
 * @Author: NGUYỄN KHÁNH DUY
 * @Date: 18/10/2022
 */
class GetLinkAvatarApi : BaseApi() {

    var params: Params = Params()

    class Params {

        @HttpRename("medias")
        var medias: ArrayList<Medias> = ArrayList()

    }

    companion object {
        fun params(medias: ArrayList<Medias>): BaseApi {
            val data = GetLinkAvatarApi()
            data.params.medias = medias
            data.requestUrl = "/api/v1/media/generate"
            data.projectId = BuildConfig.SERVER_UPLOAD
            data.authorization  = UserCache.getNodeToken()
            data.httpMethod = AppConstants.HTTP_METHOD_POST
            return data
        }
    }
}