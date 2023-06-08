package vn.hitu.ntb.other.sticker

import android.view.View
import android.view.ViewGroup
import com.aghajari.emojiview.sticker.Sticker
import com.aghajari.emojiview.sticker.StickerCategory
import vn.hitu.ntb.model.entity.CategorySticker

class WhatsAppStickers(var categorySticker: CategorySticker) : StickerCategory<String?> {
    override fun getStickers(): Array<Sticker<ArrayList<String>>?> {
        val stickers: Array<Sticker<ArrayList<String>>?> = arrayOfNulls(
            categorySticker.sticker.size
        )
        for (i in categorySticker.sticker.indices) {
            stickers[i] = Sticker<ArrayList<String>>(ArrayList())
            stickers[i]!!.data =  ArrayList()
            stickers[i]!!.data.add(categorySticker.sticker[i].media.original.url)
            stickers[i]!!.data.add(categorySticker.sticker[i].stickerId)
        }
        return stickers
    }

    override fun getCategoryData(): String {
        return categorySticker.media.original.url
    }

    override fun useCustomView(): Boolean {
        return false
    }

    override fun getView(viewGroup: ViewGroup): View? {
        return null
    }

    override fun bindView(view: View) {}
    override fun getEmptyView(viewGroup: ViewGroup): View? {
        return null
    }
}