package vn.hitu.ntb.model.entity

import com.google.gson.annotations.SerializedName

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/12/2022
 */
class CountData {
    @SerializedName("no_of_request")
    var noOfRequest = 0

    @SerializedName("no_of_received")
    var noOfReceived = 0
}