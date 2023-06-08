package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.R
import vn.hitu.ntb.databinding.DialogChooseFeedbackBinding


class DialogChooseFeedBack {
    class Builder constructor(
        context: Context
    ) : BaseDialog.Builder<Builder>(context), Runnable {
        private var binding = DialogChooseFeedbackBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null
        private var type = 0
        private var contentType = ""

        init {
            setAnimStyle(AnimAction.ANIM_TOAST)
            setGravity(Gravity.CENTER)
            setCancelable(true)
            setOnClickListener(
                binding.tvType1,
                binding.tvType2,
                binding.tvType3
            )
            setContentView(binding.root)

        }

        override fun onClick(view: View) {
            when (view) {
                binding.tvType1 -> {
                    dismiss()
                    type = 1
                    contentType = getString(R.string.content_feedback_1)!!
                    listener?.onType(getDialog()!!,type,contentType)
                }

                binding.tvType2 -> {
                    type = 2
                    dismiss()
                    contentType = getString(R.string.content_feedback_2)!!
                    listener?.onType(getDialog()!!,type,contentType)
                }

                binding.tvType3 -> {
                    type = 3
                    dismiss()
                    contentType = getString(R.string.content_feedback_3)!!
                    listener?.onType(getDialog()!!,type,contentType)
                }

            }
        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        override fun run() {
        }

    }

    interface OnListener {
        /**
         * bắn interface qua fragment xử lý
         */
        fun onType(dialog: BaseDialog, type: Int, contentType: String)
    }
}