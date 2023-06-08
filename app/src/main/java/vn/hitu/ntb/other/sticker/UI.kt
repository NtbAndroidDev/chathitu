package vn.hitu.ntb.other.sticker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.aghajari.emojiview.AXEmojiManager
import com.aghajari.emojiview.listener.OnStickerActions
import com.aghajari.emojiview.sticker.Sticker
import com.aghajari.emojiview.utils.Utils
import com.aghajari.emojiview.view.*
import vn.hitu.ntb.R
import vn.hitu.ntb.model.entity.CategorySticker

/**
 * @Author: Bùi Hữu Thắng
 * @Date: 30/08/2022
 */
object UI {
    var mEmojiView = false
    var mSingleEmojiView = false
    var mStickerView = false
    var mCustomView = false
    var mFooterView = false
    var mCustomFooter = false
    var mWhiteCategory = false
    var categoryStickers: List<CategorySticker> = ArrayList()
    @JvmField
    var darkMode = false
    fun loadTheme() {
        // release theme
        darkMode = false
        AXEmojiManager.resetTheme()

        // set EmojiView Theme
        AXEmojiManager.getEmojiViewTheme().isFooterEnabled =
            mFooterView && !mCustomFooter
        AXEmojiManager.getEmojiViewTheme().selectionColor = -0xbf7f
        AXEmojiManager.getEmojiViewTheme().footerSelectedItemColor = -0xbf7f
        AXEmojiManager.getStickerViewTheme().selectionColor = -0xbf7f
        if (mWhiteCategory) {
            AXEmojiManager.getEmojiViewTheme().selectionColor = Color.TRANSPARENT
            AXEmojiManager.getEmojiViewTheme().selectedColor = -0xbf7f
            AXEmojiManager.getEmojiViewTheme().categoryColor = Color.WHITE
            AXEmojiManager.getEmojiViewTheme().footerBackgroundColor = Color.WHITE
            AXEmojiManager.getEmojiViewTheme().setAlwaysShowDivider(true)
            AXEmojiManager.getStickerViewTheme().selectedColor = -0xbf7f
            AXEmojiManager.getStickerViewTheme().categoryColor = Color.WHITE
            AXEmojiManager.getStickerViewTheme().setAlwaysShowDivider(true)
        }
        AXEmojiManager.setBackspaceCategoryEnabled(!mCustomFooter)
    }

    @JvmName("setCategoryStickers1")
    fun setCategoryStickers(categoryStickers: List<CategorySticker>) {
        UI.categoryStickers = categoryStickers
    }

    fun loadView(context: Context, editText: EditText?): AXEmojiPager {
        val emojiPager = AXEmojiPager(context)
        if (mSingleEmojiView) {
            val singleEmojiView = AXSingleEmojiView(context)
            emojiPager.addPage(singleEmojiView, R.drawable.ic_msg_panel_smiles)
        }
        if (mEmojiView) {
            val emojiView = AXEmojiView(context)
            emojiPager.addPage(emojiView, R.drawable.ic_msg_panel_smiles)
        }
        if (mStickerView) {

            val stickerView = AXStickerView(
                context, "stickers", WhatsAppProvider(
                    categoryStickers
                )
            )
            emojiPager.addPage(stickerView, R.drawable.ic_msg_panel_stickers)

            //add sticker click listener
            stickerView.setOnStickerActionsListener(object : OnStickerActions {
                override fun onClick(view: View, sticker: Sticker<ArrayList<String>>, fromRecent: Boolean) {
//                    EventBus.getDefault().post(EventBusStickerClick(sticker.data[1]))
                }

                override fun onLongClick(
                    view: View,
                    sticker: Sticker<*>?,
                    fromRecent: Boolean
                ): Boolean {
                    return false
                }
            })
        }
        emojiPager.editText = editText
        emojiPager.setSwipeWithFingerEnabled(true)
        if (mCustomFooter) {
            initCustomFooter(context, emojiPager)
        } else {
            emojiPager.setLeftIcon(R.drawable.ic_ab_search)
            emojiPager.setOnFooterItemClicked { _: View?, leftIcon: Boolean ->
                if (leftIcon) {
                    Toast.makeText(context, "Search Clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return emojiPager
    }

    fun loadView(context: Context): AXEmojiPager {
        val emojiPager = AXEmojiPager(context)
        if (mSingleEmojiView) {
            val singleEmojiView = AXSingleEmojiView(context)
            emojiPager.addPage(singleEmojiView, R.drawable.ic_msg_panel_smiles)
        }
        if (mEmojiView) {
            val emojiView = AXEmojiView(context)
            emojiPager.addPage(emojiView, R.drawable.ic_msg_panel_smiles)
        }
        if (mStickerView) {
            val stickerView = AXStickerView(
                context, "stickers", WhatsAppProvider(
                    categoryStickers
                )
            )
            emojiPager.addPage(stickerView, R.drawable.ic_msg_panel_stickers)

            //add sticker click listener
            stickerView.setOnStickerActionsListener(object : OnStickerActions {
                override fun onClick(view: View, sticker: Sticker<ArrayList<String>>, fromRecent: Boolean) {
//                    EventBus.getDefault().post(EventBusStickerClick(sticker.data[1]))
                }

                override fun onLongClick(
                    view: View,
                    sticker: Sticker<*>?,
                    fromRecent: Boolean
                ): Boolean {
                    return false
                }
            })
        }
        emojiPager.setSwipeWithFingerEnabled(true)
        if (mCustomFooter) {
            initCustomFooter(context, emojiPager)
        } else {
            emojiPager.setLeftIcon(R.drawable.ic_ab_search)
            emojiPager.setOnFooterItemClicked { _: View?, _: Boolean -> }
        }
        return emojiPager
    }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    private fun initCustomFooter(context: Context, emojiPager: AXEmojiPager) {
        val footer = FrameLayout(context)
        @SuppressLint("UseCompatLoadingForDrawables") val drawable = context.resources.getDrawable(
            R.drawable.circle_bg, null
        )
        if (darkMode) {
            DrawableCompat.setTint(DrawableCompat.wrap(drawable), -0xe4dbd3)
        }
        footer.background = drawable
        footer.elevation = Utils.dp(context, 4f).toFloat()
        val lp = AXEmojiLayout.LayoutParams(Utils.dp(context, 48f), Utils.dp(context, 48f))
        lp.rightMargin = Utils.dp(context, 12f)
        lp.bottomMargin = Utils.dp(context, 12f)
        lp.gravity = Gravity.END or Gravity.BOTTOM
        footer.layoutParams = lp
        emojiPager.setCustomFooter(footer, true)
        val img = AppCompatImageView(context)
        val lp2 = FrameLayout.LayoutParams(Utils.dp(context, 22f), Utils.dp(context, 22f))
        lp2.gravity = Gravity.CENTER
        footer.addView(img, lp2)
        val clickListener = View.OnClickListener {
            Toast.makeText(
                context,
                "Search Clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
        emojiPager.setOnEmojiPageChangedListener { emojiPager1: AXEmojiPager, base: AXEmojiBase?, _: Int ->
            val drawable1: Drawable
            if (AXEmojiManager.isAXEmojiView(base)) {
                drawable1 = context.resources.getDrawable(R.drawable.emoji_backspace, null)
                Utils.enableBackspaceTouch(footer, emojiPager1.editText)
                footer.setOnClickListener(null)
            } else {
                drawable1 = context.resources.getDrawable(R.drawable.ic_ab_search, null)
                footer.setOnTouchListener(null)
                footer.setOnClickListener(clickListener)
            }
            DrawableCompat.setTint(
                DrawableCompat.wrap(drawable1),
                AXEmojiManager.getEmojiViewTheme().footerItemColor
            )
            img.setImageDrawable(drawable1)
        }
    }
}