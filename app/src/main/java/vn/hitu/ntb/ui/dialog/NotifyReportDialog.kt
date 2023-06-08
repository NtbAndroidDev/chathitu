package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.databinding.DialogNotifyReportBinding

/**
 * @Author:HO QUANG TUNG
 * @Date: 10/12/2022
 */
class NotifyReportDialog {
    class Builder constructor(
        context: Context
    ) : BaseDialog.Builder<Builder>(context), Runnable {

        private val binding: DialogNotifyReportBinding =
            DialogNotifyReportBinding.inflate(LayoutInflater.from(context))

        init {
            setAnimStyle(AnimAction.ANIM_SCALE)
            setGravity(Gravity.CENTER)
            setContentView(binding.root)
            binding.llCancel.setOnClickListener {
                dismiss()
            }
        }

        override fun run() {

        }
    }
}