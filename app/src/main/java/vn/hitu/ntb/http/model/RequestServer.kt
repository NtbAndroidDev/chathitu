package vn.hitu.ntb.http.model

import vn.hitu.ntb.other.AppConfig
import com.hjq.http.config.IRequestServer
import com.hjq.http.model.BodyType

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 03/10/2022
 */
class RequestServer : IRequestServer {

    override fun getHost(): String {
        return AppConfig.getHostUrl()
    }

    fun getPath(): String {
        return "api/"
    }

    fun getType(): BodyType {
        // 以表单的形式提交参数
        return BodyType.JSON
    }
}