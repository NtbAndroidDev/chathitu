package vn.hitu.ntb.contact.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.interfaces.AddFriendInterface
import vn.hitu.ntb.interfaces.MyFriendInterface
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.utils.AppUtils
import vn.techres.line.contact.R
import vn.techres.line.contact.databinding.ItemContactBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/12/2022
 */
class ContactAdapter constructor(context: Context) : AppAdapter<Friend>(context) {
    private var myFriendInterFace: MyFriendInterface? = null
    private var onClickMore: MyFriendInterface? = null
    private var onClickWall: MyFriendInterface? = null
    private var onClickMessage: MyFriendInterface? = null
    private var clickAddFriend: AddFriendInterface? = null
    private var clickRecallFriend: AddFriendInterface? = null
    private var clickFeedBack: AddFriendInterface? = null

    fun setMyFriendInterFace(myFriendInterFace: MyFriendInterface) {
        this.myFriendInterFace = myFriendInterFace
    }

    fun setOnClickMore(onClickMore: MyFriendInterface) {
        this.onClickMore = onClickMore
        this.onClickWall = onClickMore
        this.onClickMessage = onClickMore
    }

    fun setOnClickAddFriend(clickAddFriend: AddFriendInterface) {
        this.clickAddFriend = clickAddFriend
        this.clickRecallFriend = clickAddFriend
        this.clickFeedBack = clickAddFriend

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemContactBinding) : AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            if (item.name!! == "")
                binding.txtFullName.text = item.phone
            else
                binding.txtFullName.text = item.name
            binding.txtAloName.text = String.format("%s %s", getString(R.string.name_alo_line), item.fullName)
            AppUtils.loadAvatar(binding.imgAvatar, item.avatar)
            binding.ivMore.setOnClickListener {
                onClickMore!!.clickMore(item, position)
            }
            binding.ivAddFriend.setOnClickListener {
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

            CoroutineScope(Dispatchers.IO).launch {
                if (AppApplication.contactDeviceDao!!.checkIsNew(item.phone) == 1) {
                    binding.imvNew.visibility = View.VISIBLE
                } else
                    binding.imvNew.visibility = View.GONE
            }
        }
    }

}

