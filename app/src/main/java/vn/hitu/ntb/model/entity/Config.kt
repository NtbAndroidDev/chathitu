package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

class Config {
    @SerializedName("type")
    var type: String? = ""

    @SerializedName("api_key")
    var apiKey: String? = ""

    //Đường dẫn api chat aloline
    @SerializedName("api_chat_aloline")
    var apiChatAloline: String? = ""

    //Đường dẫn api upload
    @SerializedName("api_upload")
    var apiUpload: String? = ""

    // Đường dẫn api upload
    @SerializedName("api_upload_short")
    var apiUploadShort: String? = ""

    //Đường dẫn api node
    @SerializedName("api_oauth_node")
    var apiOauthNode: String? = ""

    //Đường dẫn api ADS
    @SerializedName("ads_domain")
    var adsDomain: String? = ""

    @SerializedName("api_connection")
    var apiConnection: String? = ""

    @SerializedName("realtime_domain")
    var realtimeDomain: String? = ""


}