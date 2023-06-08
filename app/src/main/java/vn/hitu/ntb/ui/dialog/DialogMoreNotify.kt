package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.databinding.DialogMoreNewsFeedBinding

/**
 * @Author: HỒ QUANG TÙNG
 * @Date: 02/01/2022
 *
 */
class DialogMoreNotify {
    class Builder constructor(context: Context) :
        BaseDialog.Builder<Builder>(context), Runnable {
        private val binding: DialogMoreNewsFeedBinding = DialogMoreNewsFeedBinding.inflate(
            LayoutInflater.from(context)
        )
        private var listener: OnListener? = null

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            initView()
            setContentView(binding.root)
        }

        override fun run() {

        }

        /**
         * khởi tạo view
         */
        private fun initView() {

        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

    }

    interface OnListener {


    }


}
