package vn.hitu.ntb.chat.http.api

import com.hjq.http.annotation.HttpRename
import vn.hitu.ntb.http.api.BaseApi

class SendMessageApi : BaseApi() {

    @HttpRename("content")
    var content: String = ""

    override fun getApi(): String {
        return ""
    }

    companion object {
        fun params(content : String): BaseApi {
            val data = SendMessageApi()
            data.content = content
            return data
        }
    }
}