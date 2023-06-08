package vn.hitu.ntb.utils

import android.app.Activity
import android.view.View
import android.widget.EditText
import com.aghajari.emojiview.listener.SimplePopupAdapter
import com.aghajari.emojiview.view.AXEmojiPager
import com.aghajari.emojiview.view.AXEmojiPopupLayout
import vn.hitu.ntb.other.sticker.UI

object KeyboardUtils {
    fun setupKeyboardCreateReviewNewsFeed(
        activity: Activity, view1: EditText, emojiPopupLayout: AXEmojiPopupLayout
    ) {
        val emojiPager: AXEmojiPager = UI.loadView(activity, view1)
        // create emoji popup
        emojiPopupLayout.initPopupView(emojiPager)
        emojiPopupLayout.isPopupAnimationEnabled = true
        emojiPopupLayout.popupAnimationDuration = 250
        emojiPopupLayout.isSearchViewAnimationEnabled = true
        emojiPopupLayout.searchViewAnimationDuration = 250

        emojiPopupLayout.hideAndOpenKeyboard()

        view1.setOnClickListener {
            if (emojiPopupLayout.visibility == View.GONE) {
                emojiPopupLayout.visibility = View.VISIBLE
            }
            emojiPopupLayout.openKeyboard()
        }

        emojiPopupLayout.setPopupListener(object : SimplePopupAdapter() {
            override fun onShow() {
                updateButton(true)
            }

            override fun onDismiss() {
                updateButton(false)
            }

            override fun onKeyboardOpened(height: Int) {
                updateButton(false)
            }

            override fun onKeyboardClosed() {
                updateButton(emojiPopupLayout.isShowing)
            }

            private fun updateButton(emoji: Boolean) {
            }
        })
    }
}