package vn.hitu.ntb.contact.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.contact.ui.holder.HeaderRequestFriendHandle
import vn.hitu.ntb.databinding.ItemFriendBinding
import vn.hitu.ntb.interfaces.AddFriendInterface
import vn.hitu.ntb.interfaces.MyFriendInterface
import vn.hitu.ntb.model.entity.IsFriend
import vn.hitu.ntb.model.entity.RequestFriend
import vn.hitu.ntb.model.entity.UserData
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import vn.techres.line.contact.databinding.ItemViewRequestFriendBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 19/12/2022
 */
class TypeFriendAdapter constructor(
    context: Context, var requestFriendAdapter: FriendRequestAdapter
) : AppAdapter<UserData>(context) {
    private var myFriendInterFace: MyFriendInterface? = null
    private var onClickMore: MyFriendInterface? = null
    private var onClickWall: MyFriendInterface? = null
    private var onClickMessage: MyFriendInterface? = null
    private var clickAddFriend: AddFriendInterface? = null
    private var clickRecallFriend: AddFriendInterface? = null
    private var clickFeedBack: AddFriendInterface? = null

    private var numberRequestFriend = 0
    private var numberMyFriend = 0

    fun setNumberRequestFriend(number: Int) {
        this.numberRequestFriend = number
    }

    fun getNumberRequestFriend(): Int {
        return numberRequestFriend
    }

    fun setNumberMyFriend(number: Int) {
        this.numberMyFriend = number
    }

    fun getNumberMyFriend(): Int {
        return numberMyFriend
    }

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

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return 0
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return if (viewType == 0) {

            val binding = ItemViewRequestFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            RequestFriendViewHolder(binding)
        } else {

            val binding =
                ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyFriendViewHolder(binding)
        }

    }

    inner class MyFriendViewHolder(private val binding: ItemFriendBinding) :
        AppViewHolder(binding.root) {
        val mAuth = FirebaseAuth.getInstance()
        private val mDatabase = FirebaseDatabase.getInstance()
        private val friendRef = mDatabase.getReference("Friends")
         private val requestRef = mDatabase.getReference("Requests")
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            binding.txtFullName.text = item.name
            AppUtils.loadAvatar(binding.imgAvatar, item.image)
//            if (item.mutualFriend != 0) {
//                binding.tvMutualFriend.text =
//                    String.format("%s %s %s", "Có", item.mutualFriend, "bạn chung")
//            } else {
//                binding.tvMutualFriend.hide()
//            }

            binding.ivMore.setOnClickListener {
//                onClickMore!!.clickMore(item, position)
            }
            binding.ivAddFriend.setOnClickListener { //gừi lời mời kết bạn
                clickAddFriend!!.clickAddFriend(item.uid)
                binding.ivAddFriend.hide()
                binding.ivRecall.show()
            }
            binding.ivRecall.setOnClickListener {
                clickRecallFriend!!.clickRecall(item.uid) //thu hồi lời mời
                binding.ivAddFriend.show()
                binding.ivRecall.hide()
            }
            binding.ivFeedBack.setOnClickListener {  //phản hồi
                clickFeedBack!!.clickFeedBack(item.uid, position)

            }
            binding.ivMessage.setOnClickListener {
                onClickMessage!!.clickMessage(position)
            }
            itemView.setOnClickListener {
                onClickWall!!.clickWall(position)
            }
            friendRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    if (dataSnapshot.exists()){
                        for (data in dataSnapshot.children){
                            val user = data.getValue(IsFriend::class.java)
                            if (user!!.mId == mAuth.currentUser!!.uid && user.yId == item.uid){
                                binding.ivAddFriend.hide()
                                binding.ivRecall.hide()
                                binding.ivMessage.show()
                                binding.ivMore.show()
                                binding.ivFeedBack.hide()
                                break
                            }
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
            requestRef.child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()){
                        val data = dataSnapshot.getValue(RequestFriend::class.java)
                        when(data!!.contactType){
                            3 -> {
                                binding.ivAddFriend.hide()
                                binding.ivRecall.show()
                                binding.ivMessage.show()
                                binding.ivMore.hide()
                                binding.ivFeedBack.hide()
                            }

                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
            requestRef.child(item.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()){
                        val data = dataSnapshot.getValue(RequestFriend::class.java)



                        when(data!!.contactType){
                            3 -> {
                                binding.ivAddFriend.hide()
                                binding.ivRecall.hide()
                                binding.ivMessage.show()
                                binding.ivMore.hide()
                                binding.ivFeedBack.show()
                            }

                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        }
    }

    inner class RequestFriendViewHolder(var binding: ItemViewRequestFriendBinding) :
        AppViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {

            HeaderRequestFriendHandle(
                binding,
                requestFriendAdapter,
                this@TypeFriendAdapter
            ).setData()
        }
    }

}