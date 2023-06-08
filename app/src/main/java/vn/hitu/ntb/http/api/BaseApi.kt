package vn.hitu.ntb.http.api

import com.hjq.http.annotation.HttpHeader
import com.hjq.http.annotation.HttpRename
import com.hjq.http.config.IRequestApi
import com.hjq.http.config.IRequestType
import com.hjq.http.model.BodyType
import vn.hitu.ntb.BuildConfig
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AppConstants


/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
open class BaseApi : IRequestApi, IRequestType {

    override fun getApi(): String {
        return "api/queues"
    }

    override fun getBodyType(): BodyType {
        return BodyType.JSON
    }

    @HttpRename("request_url")
    lateinit var requestUrl: String

    @HttpRename("project_id")
    var projectId: Int = BuildConfig.SERVER_ALOLINE

    @HttpRename("http_method")
    var httpMethod: Int? = AppConstants.HTTP_METHOD_GET

    @HttpRename("os_name")
    var osName: String? = AppConstants.APP_OS_NAME

    @HttpRename("is_production_mode")
    var isProductionMode: Int? = BuildConfig.PRODUCTION_MODE

    @HttpHeader
    @HttpRename("Authorization")
    var authorization: String? = ""
}