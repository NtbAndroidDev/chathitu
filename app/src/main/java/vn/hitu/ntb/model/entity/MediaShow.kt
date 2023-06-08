package vn.hitu.ntb.model.entity

import vn.hitu.ntb.constants.UploadTypeConstants

class MediaShow {
    var url: String = ""
    var urlLocal: String = ""
    var name: String = ""
    var type: Int = UploadTypeConstants.UPLOAD_IMAGE

    constructor(url: String, name: String, type: Int) {
        this.url = url
        this.name = name
        this.type = type
    }

    constructor(url: String, urlLocal: String, name: String, type: Int) {
        this.url = url
        this.urlLocal = urlLocal
        this.name = name
        this.type = type
    }


}