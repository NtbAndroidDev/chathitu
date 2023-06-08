package vn.hitu.ntb.chat.ui.fragment


import android.animation.Animator
import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import okhttp3.Call
import vn.hitu.ntb.app.AppFragment
import vn.hitu.ntb.cache.Auth
import vn.hitu.ntb.chat.databinding.FragmentCallHistoryBinding
import vn.hitu.ntb.chat.http.api.SendMessageApi
import vn.hitu.ntb.model.entity.ChatMessage
import vn.hitu.ntb.chat.ui.adapter.ChatGptAdapter
import vn.hitu.ntb.model.entity.ChatGpt
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import java.lang.Exception
import java.util.HashMap

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 29/05/2023
 */

class CallHistoryFragment : AppFragment<HomeActivity>(), ChatGptAdapter.ChatHandle{

    private lateinit var binding: FragmentCallHistoryBinding
    private var messageList: ArrayList<ChatGpt> = ArrayList()
    private var chatGptAdapter: ChatGptAdapter? = null
    private var databaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null


    override fun getLayoutView(): View {
        binding = FragmentCallHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        chatGptAdapter = ChatGptAdapter(getAttachActivity()!!)
        chatGptAdapter!!.setHandleMessage(this)
        binding.rcvListChat.setHasFixedSize(true)
        binding.rcvListChat.setItemViewCacheSize(100)
        chatGptAdapter!!.setData(messageList)
        val linearLayoutManager = LinearLayoutManager(
            getAttachActivity()!!, RecyclerView.VERTICAL, false
        )
        linearLayoutManager.stackFromEnd = true
        binding.rcvListChat.layoutManager = linearLayoutManager
        binding.rcvListChat.adapter = chatGptAdapter

    }

    override fun initData() {


        binding.btnSend.setOnClickListener {
            if (binding.etInputMessage.text.toString().isNotEmpty())
                sendMessage(binding.etInputMessage.text.toString())
        }

        getMessageChatGPT()
        binding.itemTyping.typing.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                binding.itemTyping.typing.playAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun getMessageChatGPT(){
        showDialog()
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatGPT").child(Auth.getAuth())
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    messageList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val chat = dataSnapshot.getValue(ChatGpt::class.java)
                        messageList.add(chat!!)
                    }
                    chatGptAdapter!!.notifyDataSetChanged()
                    binding.rcvListChat.scrollToPosition(messageList.size - 1)
                }
                hideDialog()

            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun sendMessage(content: String){
        EasyHttp.post(this)
            .api(SendMessageApi.params(content))
            .request(object : HttpCallback<ChatGpt>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onStart(call: Call?) {

                    binding.lnTyping.show()
                    saveMessage(ChatGpt(content))

                    binding.etInputMessage.setText("")
                    chatGptAdapter!!.notifyDataSetChanged()
                    binding.rcvListChat.scrollToPosition(messageList.size - 1)
                    hideKeyboard(binding.etInputMessage)
                }
                @SuppressLint("NotifyDataSetChanged")
                override fun onSucceed(data: ChatGpt) {
                    saveMessage(data)
                    binding.lnTyping.hide()

                }

                override fun onEnd(call: Call?) {
                    binding.lnTyping.hide()
                }

                override fun onFail(e: Exception?) {
                    binding.lnTyping.hide()
                }

            })
    }

    override fun onClickItem(item: ChatMessage) {

    }

    override fun onLongClickItem(item: ChatMessage) {
    }

    private fun saveMessage(content : ChatGpt){
        val ref = FirebaseDatabase.getInstance().reference
        val messageId = ref.child("ChatGPT").push().key
        val chatGpt : MutableMap<String, Any> = HashMap()
        chatGpt["message"] = content.message
        chatGpt["type"] = content.type
        val messUpdates : MutableMap<String, Any> = HashMap()
        messUpdates["/ChatGPT/${Auth.getAuth()}/$messageId"] = chatGpt
        ref.updateChildren(messUpdates)
    }
}