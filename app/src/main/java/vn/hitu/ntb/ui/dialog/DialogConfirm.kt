package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.databinding.DialogCofirmBinding
import vn.hitu.ntb.model.entity.Friend


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/1/22
 */
class DialogConfirm {
    class Builder constructor(
        context: Context,
        data : Friend
    ) : CommonDialog.Builder<Builder>(context) {
        private var binding: DialogCofirmBinding =
            DialogCofirmBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null
        private var confirm: Int = 0
        private var item = data


        init {
            setButtonView(false)
            setCancelable(true)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            binding.tvName.text =" " + item.fullName
            binding.tvUiCancel.setOnClickListener {
                dismiss()
            }
            binding.tvUiConfirm.setOnClickListener {
                confirm = 1
                listener!!.onConfirm(getDialog()!!, confirm)
                dismiss()

            }
            setCustomView(binding.root)

        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }
    }

    interface OnListener {

        /**
         * bắn interface qua fragment xử lý
         */
        fun onConfirm(dialog: BaseDialog, confirm: Int)

    }
}