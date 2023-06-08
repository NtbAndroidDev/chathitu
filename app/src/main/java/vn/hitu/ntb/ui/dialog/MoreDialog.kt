package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.hitu.base.BaseDialog
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.R
import vn.hitu.ntb.constants.AccountConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.databinding.ItemMoreDialogBinding
import vn.hitu.ntb.model.entity.Friend

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 11/30/22
 */
class MoreDialog {
    class Builder constructor(
        context: Context,
        data: Friend,
        key: Int
    ) : BaseDialog.Builder<Builder>(context), Runnable {

        private val binding: ItemMoreDialogBinding =
            ItemMoreDialogBinding.inflate(LayoutInflater.from(context))
        private var listener: OnListener? = null
        private var item = data
        private val key = key
        private var dialogConfirm: DialogConfirm.Builder? = null
        private var dialogChooseReport: DialogChooseReport.Builder? = null

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)


            if (item.contactType != 4)
                binding.clUnFriend.visibility = View.GONE
            when (key) {
                1 -> {
                    binding.clReport.visibility = View.GONE
                    binding.clTrashPost.visibility = View.GONE
                    binding.clWall.visibility = View.VISIBLE
                }
                2 -> {
                    binding.clReport.visibility = View.VISIBLE
                    binding.clUnFriend.visibility = View.VISIBLE
                    binding.clTrashPost.visibility = View.GONE
                    binding.clWall.visibility = View.GONE
                }
                else -> {
                    binding.clReport.visibility = View.VISIBLE
                    binding.clTrashPost.visibility = View.VISIBLE
                    binding.clWall.visibility = View.GONE
                }
            }

//            binding.tvPersonalPage.text = String.format("%s %s", getString(R.string.personal_page), fullName)
            binding.tvWall.text =
                String.format("%s %s", getString(R.string.personal_page), item.fullName)
            binding.tvDiary.text =
                String.format("%s %s", getString(R.string.hide_diary), item.fullName)
            binding.tvHideDiary.text =
                String.format("%s %s", getString(R.string.hide_new_feed), item.fullName)
            binding.tvBlock.text =
                String.format("%s %s", getString(R.string.block_user), item.fullName)
            binding.tvBlockNewFeed.text =
                String.format("%s %s", getString(R.string.block), item.fullName)
            binding.tvUnFriend.text =
                String.format("%s %s", getString(R.string.un_friend), item.fullName)



            binding.clWall.setOnClickListener {
                try {
                    val intent = Intent(
                        getContext(),
                        Class.forName(ModuleClassConstants.INFO_CUSTOMER)
                    )
                    val bundle = Bundle()
                    bundle.putInt(
                        AccountConstants.ID, item.userId
                    )
                    intent.putExtras(bundle)
                    startActivity(intent)
                    dismiss()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            binding.clBlock.setOnClickListener {
                listener!!.onBlockUser(getDialog()!!, item.userId)
            }
            binding.clUnFriend.setOnClickListener {
                dismiss()
                dialogConfirm = DialogConfirm.Builder(context, item)
                    .setListener(object : DialogConfirm.OnListener {
                        override fun onConfirm(dialog: BaseDialog, confirm: Int) {
                            if (confirm == 1)
                                listener!!.onUnFriendUser(getDialog()!!, item.userId)
                        }
                    })
                dialogConfirm!!.show()
            }
            binding.clReport.setOnClickListener {
                dismiss()
                dialogChooseReport = DialogChooseReport.Builder(context, item)
                    .setListener(object : DialogChooseReport.OnListener {
                        override fun onContentType(
                            dialog: BaseDialog,
                            type: Int,
                            contentReport: String
                        ) {
                            if (type != 0) {
                                listener!!.onContentReport(getDialog()!!, type, contentReport)
                            }
                        }

                    })
                dialogChooseReport!!.show()
            }
            setContentView(binding.root)
        }

        override fun run() {

        }

        fun setListener(listener: OnListener): Builder = apply {
            this.listener = listener
        }


    }

    interface OnListener {

        /**
         * bắn interface qua fragment xử lý
         */
        fun onBlockUser(dialog: BaseDialog, idUser: Int)
        fun onUnFriendUser(dialog: BaseDialog, idUser: Int)
        fun onContentReport(dialog: BaseDialog, type: Int, contentReport: String)

    }
}