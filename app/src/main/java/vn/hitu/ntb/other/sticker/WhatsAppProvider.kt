package vn.hitu.ntb.other.sticker

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.aghajari.emojiview.AXEmojiManager
import com.aghajari.emojiview.sticker.Sticker
import com.aghajari.emojiview.sticker.StickerCategory
import com.aghajari.emojiview.sticker.StickerLoader
import com.aghajari.emojiview.sticker.StickerProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import vn.hitu.ntb.model.entity.CategorySticker
import vn.hitu.ntb.utils.AppUtils.getLinkPhoto

class WhatsAppProvider(var data: List<CategorySticker>) : StickerProvider {
    var category: Array<StickerCategory<*>?> = arrayOfNulls(data.size)

    override fun getCategories(): Array<StickerCategory<*>?> {
        for (i in data.indices) {
            category[i] = WhatsAppStickers(data[i])
        }
        return category
    }


    override fun getLoader(): StickerLoader {
        return object : StickerLoader {
            override fun onLoadSticker(view: View, sticker: Sticker<ArrayList<String>>) {
//                if (sticker.isInstance(String::class.java)) {
//                    Glide.with(view).load(getLinkPhoto(sticker.data[0]))
//                        .apply(RequestOptions.fitCenterTransform()).into(
//                        (view as AppCompatImageView)
//                    )
//                }
                Glide.with(view).load(getLinkPhoto(sticker.data[0], "images"))
                    .apply(RequestOptions.fitCenterTransform()).into(
                        (view as AppCompatImageView)
                    )
            }

            override fun onLoadStickerCategory(
                view: View,
                stickerCategory: StickerCategory<*>,
                selected: Boolean
            ) {
                try {
                    if (stickerCategory is ShopStickers) {
                        val dr0 = AppCompatResources.getDrawable(
                            view.context,
                            stickerCategory.categoryData
                        )
                        val dr = dr0!!.constantState!!.newDrawable()
                        if (selected) {
                            DrawableCompat.setTint(
                                DrawableCompat.wrap(dr),
                                AXEmojiManager.getStickerViewTheme().selectedColor
                            )
                        } else {
                            DrawableCompat.setTint(
                                DrawableCompat.wrap(dr),
                                AXEmojiManager.getStickerViewTheme().defaultColor
                            )
                        }
                        (view as AppCompatImageView).setImageDrawable(dr)
                    } else {
                        Glide.with(view).load(getLinkPhoto(stickerCategory.categoryData as String, "images"))
                            .apply(RequestOptions.fitCenterTransform()).into(
                            (view as AppCompatImageView)
                        )
                    }
                } catch (ignore: Exception) {
                }
            }
        }
    }

    override fun isRecentEnabled(): Boolean {
        return true
    }
}