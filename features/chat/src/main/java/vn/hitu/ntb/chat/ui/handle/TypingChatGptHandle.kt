package vn.hitu.ntb.chat.ui.handle

import android.animation.Animator
import vn.hitu.ntb.chat.databinding.ChatGptTypingBinding
import vn.hitu.ntb.chat.ui.adapter.ChatGptAdapter
import vn.hitu.ntb.model.entity.ChatGpt

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 26/04/2023
 */
class TypingChatGptHandle(
    private val binding: ChatGptTypingBinding,
    private val data: ChatGpt,
    private val position: Int,
    private val chatHandle: ChatGptAdapter.ChatHandle,
    private var adapter: ChatGptAdapter) {

    fun setData(){

        binding.typing.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                binding.typing.playAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }


}