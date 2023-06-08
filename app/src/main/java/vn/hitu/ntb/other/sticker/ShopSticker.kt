package vn.hitu.ntb.other.sticker

import com.aghajari.emojiview.sticker.Sticker

/**
 * @Author: Bùi Hữu Thắng
 * @Date: 30/08/2022
 */
class ShopSticker(data: Array<Sticker<*>?>?, var title: String, var count: Int) :
    Sticker<Any?>(data) {
    companion object {
        private const val serialVersionUID = 3L
    }
}