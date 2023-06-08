package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.databinding.DialogAgreeBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 25/05/2023
 */
class DialogAgree {
    class Builder constructor(
        context: Context,
        headerText: String,
        messageText: String
    ) :
        BaseDialog.Builder<Builder>(context) {
        private var binding: DialogAgreeBinding =
            DialogAgreeBinding.inflate(LayoutInflater.from(context))

        lateinit var onActionDone: OnActionDone

        fun onActionDone(onActionDone: OnActionDone): Builder = apply {
            this.onActionDone = onActionDone
        }

        fun setCancel(text : String) : Builder = apply {
            binding.btnCancel.text = text
        }
        fun setConfirm(text : String): Builder = apply{
            binding.btnConfirm.text = text
        }

        init {
            setCancelable(false)
            setAnimStyle(AnimAction.ANIM_SCALE)
            setGravity(Gravity.CENTER)
            setContentView(binding.root)
            binding.tvHeader.text = headerText
            binding.tvContent.text = messageText
            binding.btnCancel.setOnClickListener {
                binding.btnCancel.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.btnCancel.isEnabled = true
                }, 1000)
                dismiss()
                onActionDone.onActionDone(false)
            }
            binding.btnConfirm.setOnClickListener {
                binding.btnConfirm.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.btnConfirm.isEnabled = true
                }, 1000)
                dismiss()
                onActionDone.onActionDone(true)
            }
        }

        interface OnActionDone {
            fun onActionDone(isConfirm: Boolean)
        }
    }
}