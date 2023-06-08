package vn.hitu.ntb.model.entity

import java.util.ArrayList

class ImageClip {
    constructor(stringList: ArrayList<String>, time: Int) {
        this.stringList = stringList
        this.time = time
    }

    constructor() {
        stringList = ArrayList()
        time = 0
    }

    var stringList: ArrayList<String> = ArrayList()
    var time = 0
}