package vn.hitu.ntb.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.hitu.base.BaseAdapter
import vn.hitu.base.action.AnimAction
import vn.hitu.ntb.databinding.DialogFeedbackFriendBinding


class DialogFeedBack {
    class Builder(
        context: Context
    ) : CommonDialog.Builder<Builder>(context), BaseAdapter.OnItemClickListener {
        private var listener: ClickAddFriend? = null

        private val binding: DialogFeedbackFriendBinding = DialogFeedbackFriendBinding.inflate(
            LayoutInflater.from(context)
        )


        fun setListener(listener: ClickAddFriend): Builder = apply {
            this@Builder.listener = listener
        }

        init {
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
            initView()
            setContentView(binding.root)
            binding.llConfirmAddFriend.setOnClickListener {
                listener!!.onClickAddFriend(1)
                autoDismiss()
            }
            binding.llCancelAddFriend.setOnClickListener {
                listener!!.onClickAddFriend(2)
                autoDismiss()
            }
        }

        /**
         * khởi tạo view
         */
        private fun initView() {

        }

        override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
            TODO("Not yet implemented")
        }


    }

    interface ClickAddFriend {
        fun onClickAddFriend(type : Int)
    }


}