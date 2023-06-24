package hitu.ntb.story.ui.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.animation.addListener
import hitu.ntb.story.databinding.ItemCountDownBinding
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.model.entity.Story
import vn.hitu.ntb.utils.AudioUtils

class CountdownAdapter (context: Context) : AppAdapter<Story>(context) {


    private var listener: OnListener? = null
    fun setOnListener(onListener: OnListener) {
        this.listener = onListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {

        val binding = ItemCountDownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    inner class ViewHolder(private val binding: ItemCountDownBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        override fun onBindView(position: Int) {
            setIsRecyclable(false)
            val item = getItem(position)
            val set1 = AnimatorSet()

            AudioUtils.setProcessBarForCountDown(set1, binding.pgbCountDown)
            val layoutParams = binding.pgbCountDown.layoutParams as LinearLayout.LayoutParams

            layoutParams.width = (getResources().displayMetrics.widthPixels / getData().size)

            binding.pgbCountDown.layoutParams = layoutParams


            if (item.currentItem == 1) {
                binding.pgbCountDown.progress = 0
                set1.start()
                set1.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
                        Toast.makeText(getContext(), "onAnimationStart", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAnimationEnd(p0: Animator?) {

//                        item.currentItem = 0
//                        item.done = 1
//                        binding.pgbCountDown.progress = 100
//                        set1.cancel()
//                        listener!!.startProgress(position)

                        Toast.makeText(getContext(), "onAnimationEnd", Toast.LENGTH_SHORT).show()
                        return


                    }

                    override fun onAnimationCancel(p0: Animator?) {


                        Toast.makeText(getContext(), "onAnimationCancel", Toast.LENGTH_SHORT).show()

                    }

                    override fun onAnimationRepeat(p0: Animator?) {
                        Toast.makeText(getContext(), "onAnimationRepeat", Toast.LENGTH_SHORT).show()

                    }

                })

            }else{
                set1.cancel()
            }


        }


    }
    interface OnListener {
        fun startProgress(position: Int)

    }


}