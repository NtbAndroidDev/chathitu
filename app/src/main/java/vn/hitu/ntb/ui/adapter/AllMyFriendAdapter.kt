package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.constants.AppConstants

import vn.hitu.ntb.databinding.ItemFriendBinding
import vn.hitu.ntb.interfaces.AddFriendInterface
import vn.hitu.ntb.interfaces.MyFriendInterface
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.utils.AppUtils

/**
 * @Author: Phạm Văn Nhân
 * @Date: 04/10/2022
 * @author: NGUYEN THANH BINH
 * @Date: 29/11/2022
 */
class AllMyFriendAdapter constructor(context: Context) : AppAdapter<Friend>(context){
    private var myFriendInterFace : MyFriendInterface? = null
    private var onClickMore : MyFriendInterface? = null
    private var onClickWall : MyFriendInterface? = null
    private var onClickMessage : MyFriendInterface? = null
    private var clickAddFriend : AddFriendInterface? = null
    private var clickRecallFriend : AddFriendInterface? = null
    private var clickFeedBack : AddFriendInterface? = null

    fun setMyFriendInterFace(myFriendInterFace: MyFriendInterface){
        this.myFriendInterFace = myFriendInterFace
    }
    fun setOnClickMore(myFriendInterface: MyFriendInterface){
        this.onClickMore = myFriendInterface
        this.onClickWall = myFriendInterface
        this.onClickMessage = myFriendInterface
    }

    fun setOnClickAddFriend(clickAddFriend: AddFriendInterface){
        this.clickAddFriend = clickAddFriend
        this.clickRecallFriend = clickAddFriend
        this.clickFeedBack = clickAddFriend

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemFriendBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            binding.txtFullName.text = item.fullName
            AppUtils.loadImageUser(binding.imgAvatar, item.avatar)
            binding.tvMutualFriend.text = String.format("%s %s %s", "Có", item.mutualFriend, "bạn chung")
            binding.ivMore.setOnClickListener {
                onClickMore!!.clickMore(item, position)
            }
            binding.ivAddFriend.setOnClickListener{
//                clickAddFriend!!.clickAddFriend(item.userId)
                binding.ivAddFriend.visibility = View.GONE
                binding.ivRecall.visibility = View.VISIBLE
            }
            binding.ivRecall.setOnClickListener {
//                clickRecallFriend!!.clickRecall(item.userId)
                binding.ivAddFriend.visibility = View.VISIBLE
                binding.ivRecall.visibility = View.GONE
            }
            binding.ivFeedBack.setOnClickListener {
//                clickFeedBack!!.clickFeedBack(item.userId, position)

            }
            binding.ivMessage.setOnClickListener {
                onClickMessage!!.clickMessage(position)
            }
            itemView.setOnClickListener {
                onClickWall!!.clickWall(position)
            }
            when (item.contactType) {
                AppConstants.ITS_ME -> {
                    binding.ivAddFriend.visibility = View.GONE
                    binding.ivMore.visibility = View.GONE
                    binding.ivMessage.visibility = View.GONE
                    binding.ivRecall.visibility = View.GONE
                    binding.ivFeedBack.visibility = View.GONE
                }
                AppConstants.FRIEND -> {
                    binding.ivAddFriend.visibility = View.GONE
                    binding.ivRecall.visibility = View.GONE
                    binding.ivMessage.visibility = View.VISIBLE
                    binding.ivMore.visibility = View.VISIBLE
                    binding.ivFeedBack.visibility = View.GONE

                }
                AppConstants.WAITING_RESPONSE -> {
                    binding.ivAddFriend.visibility = View.GONE
                    binding.ivRecall.visibility = View.VISIBLE
                    binding.ivMessage.visibility = View.VISIBLE
                    binding.ivMore.visibility = View.GONE
                    binding.ivFeedBack.visibility = View.GONE

                }
                AppConstants.NOT_FRIEND -> {
                    binding.ivAddFriend.visibility = View.VISIBLE
                    binding.ivRecall.visibility = View.GONE
                    binding.ivMessage.visibility = View.VISIBLE
                    binding.ivMore.visibility = View.GONE
                    binding.ivFeedBack.visibility = View.GONE
                }
                else -> {
                    binding.ivAddFriend.visibility = View.GONE
                    binding.ivRecall.visibility = View.GONE
                    binding.ivMessage.visibility = View.VISIBLE
                    binding.ivMore.visibility = View.GONE
                    binding.ivFeedBack.visibility = View.VISIBLE

                }
            }

        }
    }

//    inner class HeaderViewHolder(private val binding: ItemHeaderStickyBinding) : AppViewHolder(binding.root) {
//        @RequiresApi(Build.VERSION_CODES.N)
//        override fun onBindView(position: Int) {
//
//            binding.tvHeader.text = UCharacter.toUpperCase(getItem(position).fullName!!.substring(0, 1))
//            //binding.tvHeader.textSize = getResources().getDimension(vn.hitu.base.R.dimen.sp_14)
//        }
//
//    }

}