package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.databinding.DialogChooseContentReportBinding
import vn.hitu.ntb.model.entity.Friend


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/7/22
 */
class DialogChooseReport {
    class Builder constructor(
        context: Context,
        data : Friend
    ) : BaseDialog.Builder<Builder>(context), Runnable {
        private var binding = DialogChooseContentReportBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null
        private var type = 0
        private var contentReport = ""
        private var item = data
        private var dialogConfirm: DialogConfirm.Builder? = null

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setCancelable(true)
            setOnClickListener(
                binding.tvType1,
                binding.tvType2,
                binding.tvType3,
                binding.tvType4,
                binding.tvType5,
                binding.tvType6,
                binding.tvType7,
                binding.tvType8,
                binding.tvType9,
                binding.btnClose
            )
            setContentView(binding.root)

        }

        override fun onClick(view: View) {
            when (view) {
                binding.tvType1 -> {
                    dismiss()
                    type = 1
                    contentReport = "Thông tin không chính xác"
                    dialogConfirm(type, contentReport)


                }

                binding.tvType2 -> {
                    type = 2
                    dismiss()
                    contentReport = "Ngôn từ gây thù ghét"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType3 -> {
                    type = 3
                    dismiss()
                    contentReport = "Nội dung về tình dục hoặc ảnh khoả thân"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType4 -> {
                    type = 4
                    dismiss()
                    contentReport = "Bạo lực"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType5 -> {
                    type = 5
                    dismiss()
                    contentReport = "Thông tin sai sự thật"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType6 -> {
                    type = 6
                    dismiss()
                    contentReport = "Quấy rối"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType7 -> {
                    type = 7
                    dismiss()
                    contentReport = "Trang lừa đảo và giả mạo"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType8 -> {
                    type = 8
                    dismiss()
                    contentReport = " Bán hàng trái phép"
                    dialogConfirm(type, contentReport)

                }

                binding.tvType9 -> {
                    type = 9
                    dismiss()
                    contentReport = "Quyền sở hữu trí tuệ"
                    dialogConfirm(type, contentReport)

                }

                binding.btnClose -> {
                    dismiss()
                }
            }
        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }

        override fun run() {
        }

        private fun dialogConfirm(type: Int, contentReport: String) {
            dialogConfirm = DialogConfirm.Builder(getContext(), item)
                .setListener(object : DialogConfirm.OnListener {
                    override fun onConfirm(
                        dialog: BaseDialog,
                        confirm: Int
                    ) {
                        if (confirm == 1) {
                            listener!!.onContentType(getDialog()!!, type, contentReport)
                            dismiss()
                        }
                    }

                })
            dialogConfirm!!.show()

        }

    }

    interface OnListener {

        /**
         * bắn interface qua fragment xử lý
         */
        fun onContentType(dialog: BaseDialog, type: Int, contentReport: String)
    }
}