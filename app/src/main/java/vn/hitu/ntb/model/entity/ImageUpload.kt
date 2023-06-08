package vn.hitu.ntb.model.entity

class ImageUpload {
    constructor(image: String) {
        this.image = image
    }

    constructor(image: String, time: Long) {
        this.image = image
        this.time = time
    }

    var image = ""
    var time = 0L
    var isActive = false
}