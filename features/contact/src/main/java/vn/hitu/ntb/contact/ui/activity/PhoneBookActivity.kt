package vn.hitu.ntb.contact.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import vn.hitu.base.BaseAdapter
import vn.hitu.base.BaseDialog
import vn.hitu.ntb.app.AppActivity
import vn.hitu.ntb.app.AppApplication
import vn.hitu.ntb.app.AppApplication.Companion.contactDao
import vn.hitu.ntb.app.AppApplication.Companion.contactDeviceDao
import vn.hitu.ntb.cache.UserCache
import vn.hitu.ntb.constants.AccountConstants
import vn.hitu.ntb.constants.AppConstants
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.contact.api.SyncApi
import vn.hitu.ntb.contact.ui.adapter.ContactAdapter
import vn.hitu.ntb.eventbus.CurrentFragmentEventBus
import vn.hitu.ntb.eventbus.FragmentEventBus
import vn.hitu.ntb.http.api.CreateConversationApi
import vn.hitu.ntb.http.model.HttpData
import vn.hitu.ntb.interfaces.AddFriendInterface
import vn.hitu.ntb.interfaces.MyFriendInterface
import vn.hitu.ntb.model.entity.ContactDevice
import vn.hitu.ntb.model.entity.Conversation
import vn.hitu.ntb.model.entity.Friend
import vn.hitu.ntb.model.entity.FriendContactFromData
import vn.hitu.ntb.model.entity.GroupChat
import vn.hitu.ntb.other.doAfterTextChanged
import vn.hitu.ntb.ui.activity.HomeActivity
import vn.hitu.ntb.ui.dialog.DialogFeedBack
import vn.hitu.ntb.ui.dialog.MoreDialog
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.TimeFormat
import vn.techres.line.contact.R
import vn.techres.line.contact.databinding.ActivityPhoneBookBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/2/22
 */
class PhoneBookActivity : AppActivity(), BaseAdapter.OnItemClickListener, MyFriendInterface,
    AddFriendInterface, OnRefreshListener {
    private lateinit var binding: ActivityPhoneBookBinding
    private var contactList: ArrayList<ContactDevice> = ArrayList()
    private var dataList: ArrayList<Friend> = ArrayList()
    private var adapterMyContact: ContactAdapter? = null
    private var dialogMore: MoreDialog.Builder? = null
    private var positionItem = -1
    private var REQUEST_CODE = 0
    private var keySearch = ""
    private var ON_LISTENER = 1
    private var OFF_LISTENER = 0
    private var btnAll = 1
    private var btnNew = 0
    private var NOT_EMPTY = -1
    private var dialogFeedBack: DialogFeedBack.Builder? = null

    override fun getLayoutView(): View {
        binding = ActivityPhoneBookBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkContact()
    }


    override fun initView() {
        if (!UserCache.getUser().phoneBookUpdateTime.equals("")) {
            binding.tvUpdateAt.visibility = View.VISIBLE
            binding.tvUpdateAt.text = String.format(
                "%s %s",
                getString(R.string.hint_phone_book),
                UserCache.getUser().phoneBookUpdateTime
            )
        } else {
            binding.tvUpdateAt.visibility = View.GONE
        }

        binding.srlContact.setOnRefreshListener(this)
        adapterMyContact = ContactAdapter(this)
        adapterMyContact!!.setMyFriendInterFace(this)
        adapterMyContact?.setOnItemClickListener(this)
        adapterMyContact?.setOnClickAddFriend(this)
        adapterMyContact?.setOnClickMore(this)

        AppUtils.initRecyclerViewVertical(
            binding.itemRcv.recyclerViewMyFriend, adapterMyContact
        )

        adapterMyContact!!.setData(dataList)
        setOnClickListener(binding.itemRcv.tvAll, binding.itemRcv.tvNew)

        val dataSearch = ArrayList<Friend>()
        binding.itemRcv.lnEmpty.visibility = View.GONE
        binding.itemRcv.sflContact.visibility = View.VISIBLE
        binding.itemRcv.recyclerViewMyFriend.visibility = View.GONE
        binding.itemRcv.sflContact.startShimmer()

        CoroutineScope(Dispatchers.IO).launch {
            AppApplication.appDatabase!!.runInTransaction {
                dataSearch.clear()
                dataSearch.addAll(contactDao!!.filterNotFriend(keySearch))

                postDelayed({
                    binding.itemRcv.sflContact.visibility = View.GONE
                    binding.itemRcv.recyclerViewMyFriend.visibility = View.VISIBLE
                }, 1000)
                binding.itemRcv.tvNew.text = String.format(
                    "%s %s",
                    getString(R.string.not_friend),
                    dataSearch.size.toString()
                )
            }
        }
    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    override fun initData() {
        binding.itemEmpty.btnSync.setOnClickListener {
            dataList.clear()
            if (applicationContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE
                )
            }
        }

        binding.btnUpdate.setOnClickListener {
            showDialog()
            dataList.clear()
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
            binding.tvUpdateAt.visibility = View.VISIBLE

            postDelayed({
                hideDialog()
            }, 1000)
        }

        binding.lnUpdate.setOnClickListener {
            dataList.clear()
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
            binding.tvUpdateAt.visibility = View.VISIBLE

        }

        binding.svSaveBranch.doAfterTextChanged(500) {
            keySearch = it
            val dataSearch = ArrayList<Friend>()
            binding.itemRcv.lnEmpty.visibility = View.GONE
            binding.itemRcv.sflContact.visibility = View.VISIBLE
            binding.itemRcv.recyclerViewMyFriend.visibility = View.GONE
            binding.itemRcv.sflContact.startShimmer()

            CoroutineScope(Dispatchers.IO).launch {
                AppApplication.appDatabase!!.runInTransaction {
                    if (btnAll == 1) {
                        dataSearch.addAll(
                            contactDao!!.filterAll(
                                keySearch
                            )
                        )
                    } else {
                        dataSearch.addAll(
                            contactDao!!.filterNotFriend(
                                keySearch
                            )
                        )
                    }
                }
            }

            postDelayed({
                if (btnAll == 1) binding.itemRcv.tvAll.text =
                    String.format("%s %s", getString(R.string.all), dataSearch.size.toString())
                else binding.itemRcv.tvNew.text = String.format(
                    "%s %s", getString(R.string.not_friend), dataSearch.size.toString()
                )
                dataList.clear()
                dataList.addAll(dataSearch)
                adapterMyContact!!.notifyDataSetChanged()


                binding.itemRcv.sflContact.visibility = View.GONE
                binding.itemRcv.recyclerViewMyFriend.visibility = View.VISIBLE
                binding.itemRcv.sflContact.stopShimmer()

                checkEmpty(dataList)
            }, 500)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(view: View) {
        when (view) {
            binding.itemRcv.tvAll -> {
                if (btnAll == 0) {
                    selected(ON_LISTENER, OFF_LISTENER)
                    dataList.clear()
                    keySearch = ""
                    binding.svSaveBranch.setQuery("", false)
                    val dataSearch = ArrayList<Friend>()
                    binding.itemRcv.lnEmpty.visibility = View.GONE
                    binding.itemRcv.sflContact.visibility = View.VISIBLE
                    binding.itemRcv.recyclerViewMyFriend.visibility = View.GONE
                    binding.itemRcv.sflContact.startShimmer()
                    CoroutineScope(Dispatchers.IO).launch {
                        AppApplication.appDatabase!!.runInTransaction {
                            dataSearch.clear()
                            dataSearch.addAll(
                                contactDao!!.filterAll(
                                    keySearch
                                )
                            )
                            btnAll = 1
                            btnNew = 0

                        }
                    }
                    postDelayed({
                        dataList.clear()
                        adapterMyContact!!.notifyDataSetChanged()

                        dataList.addAll(dataSearch)
                        adapterMyContact!!.notifyDataSetChanged()


                        binding.itemRcv.tvAll.text = String.format("%s %s", getString(R.string.all), dataSearch.size.toString())

                        binding.itemRcv.sflContact.visibility = View.GONE
                        binding.itemRcv.recyclerViewMyFriend.visibility = View.VISIBLE
                        binding.itemRcv.sflContact.stopShimmer()

                        checkEmpty(dataList)
                    }, 500)

                }
            }

            binding.itemRcv.tvNew -> {
                if (btnNew == 0) {
                    selected(OFF_LISTENER, ON_LISTENER)
                    keySearch = ""

                    binding.svSaveBranch.setQuery("", false)



                    binding.itemRcv.lnEmpty.visibility = View.GONE
                    binding.itemRcv.sflContact.visibility = View.VISIBLE
                    binding.itemRcv.recyclerViewMyFriend.visibility = View.GONE
                    binding.itemRcv.sflContact.startShimmer()

                    val dataSearch = ArrayList<Friend>()
                    CoroutineScope(Dispatchers.IO).launch {
                        AppApplication.appDatabase!!.runInTransaction {
                            dataSearch.clear()
                            dataSearch.addAll(
                                contactDao!!.filterNotFriend(
                                    keySearch
                                )
                            )
                            btnNew = 1
                            btnAll = 0
                        }
                    }
                    postDelayed({
                        dataList.clear()
                        adapterMyContact!!.notifyDataSetChanged()

                        dataList.addAll(dataSearch)
                        adapterMyContact!!.notifyDataSetChanged()

                        adapterMyContact!!.notifyDataSetChanged()
                        binding.itemRcv.tvNew.text = String.format("%s %s", getString(R.string.not_friend), dataList.size.toString())


                        binding.itemRcv.sflContact.visibility = View.GONE
                        binding.itemRcv.recyclerViewMyFriend.visibility = View.VISIBLE
                        binding.itemRcv.sflContact.stopShimmer()

                        checkEmpty(dataList)
                    }, 500)

                }
            }
        }
    }

    /**
     * Gọi api lấy danh sách danh bạ
     */
    @SuppressLint("HardwareIds")
    private fun getContact(contact: ArrayList<ContactDevice>) {
        binding.itemRcv.lnEmpty.visibility = View.GONE
        binding.itemRcv.sflContact.visibility = View.VISIBLE
        binding.itemRcv.recyclerViewMyFriend.visibility = View.GONE
        binding.itemRcv.sflContact.startShimmer()

        EasyHttp.post(this).api(SyncApi.params(contact))
            .request(object : HttpCallback<HttpData<FriendContactFromData>>(this) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onStart(call: Call?) {

                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onSucceed(data: HttpData<FriendContactFromData>) {
                    if (data.isRequestSucceed()) {
                        dataList.clear()

                        binding.itemRcv.sflContact.visibility = View.GONE
                        binding.itemRcv.recyclerViewMyFriend.visibility = View.VISIBLE
                        binding.itemRcv.sflContact.stopShimmer()


                        //dataList.addAll(data.getData()!!.list)


                        binding.itemRcv.tvAll.text =
                            String.format("%s %s", getString(R.string.all), dataList.size)
                        binding.itemEmpty.lnEmpty.visibility = View.GONE
                        binding.lnContact.visibility = View.VISIBLE

                        CoroutineScope(Dispatchers.IO).launch {
                            AppApplication.appDatabase!!.runInTransaction {
                                contactDao!!.deleteAllData()

                                contactDao!!.insertDataAll(data.getData()!!.list)
                            }
                        }

                        postDelayed({
                            if (btnNew == 1) {
                                val dataSearch = ArrayList<Friend>()
                                CoroutineScope(Dispatchers.IO).launch {
                                    AppApplication.appDatabase!!.runInTransaction {
                                        dataSearch.clear()
                                        dataSearch.addAll(
                                            contactDao!!.filterNotFriend(
                                                keySearch
                                            )
                                        )
                                        binding.itemRcv.tvNew.text = String.format("%s %s", getString(R.string.not_friend), dataSearch.size.toString())

                                    }
                                }
                                postDelayed({
                                    dataList.clear()
                                    dataList.addAll(dataSearch)
                                    adapterMyContact!!.notifyDataSetChanged()
                                    checkEmpty(dataList)
                                }, 500)
                            } else {
                                val dataSearch = ArrayList<Friend>()
                                CoroutineScope(Dispatchers.IO).launch {
                                    AppApplication.appDatabase!!.runInTransaction {
                                        dataSearch.clear()
                                        dataSearch.addAll(
                                            contactDao!!.filterAll(
                                                keySearch
                                            )
                                        )


                                    }
                                }
                                postDelayed({
                                    binding.itemRcv.tvAll.text = String.format(
                                        "%s %s", getString(R.string.all), dataSearch.size.toString()
                                    )
                                    dataList.clear()
                                    dataList.addAll(dataSearch)
                                    adapterMyContact!!.notifyDataSetChanged()
                                    checkEmpty(dataList)
                                }, 500)
                            }
                        }, 1000)

                    }
                }
            })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {

                    val contacts = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
                    )

                    contactList.clear()

                    CoroutineScope(Dispatchers.IO).launch {
                        contactDeviceDao!!.deleteAllData()
                    }

                    val dateFormat =
                        SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

                    while (contacts!!.moveToNext()) {
                        val ojb = ContactDevice()
                        ojb.name =
                            contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        ojb.phone =
                            formatPhone(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))

                        val date =
                            contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP))
                                .toLong()
                        ojb.isNew = TimeFormat.checkTimeIn24HourPhoneBook(
                            dateFormat.format(date),
                            dateFormat.format(Date())
                        )

                        contactList.add(ojb)
                        CoroutineScope(Dispatchers.IO).launch {
                            contactDeviceDao!!.insertData(ojb)
                        }
                    }

                    contacts.close()

                    getContact(contactList)

                    val user = UserCache.getUser()
                    user.phoneBookUpdateTime = TimeFormat.getCurrentTimeFormat()
                    binding.tvUpdateAt.text = String.format("%s %s", getString(R.string.hint_phone_book), user.phoneBookUpdateTime)
                    UserCache.saveUser(user)
                } else toast(getString(R.string.permission_contact))
            }
        }
    }

    /**
     * kiểm tra đã bật quyền danh bạ chưa?
     */
    @SuppressLint("Range", "NotifyDataSetChanged", "ObsoleteSdkInt")
    private fun checkContact() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            binding.lnContact.visibility = View.VISIBLE
            binding.itemEmpty.lnEmpty.visibility = View.GONE

            val contacts = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
            )
            contactList.clear()
            while (contacts!!.moveToNext()) {
                val ojb = ContactDevice()
                ojb.name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                ojb.phone =
                    formatPhone(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
                contactList.add(ojb)
            }

            contacts.close()

            CoroutineScope(Dispatchers.IO).launch {
                AppApplication.appDatabase!!.runInTransaction {
                    NOT_EMPTY = if (contactDao!!.getAllData().isEmpty()) 0
                    else 1

                }
            }

            if (NOT_EMPTY == 0) {
                getContact(contactList)
            }

            postDelayed({
                if (btnNew == 1) {
                    val dataSearch = ArrayList<Friend>()
                    CoroutineScope(Dispatchers.IO).launch {
                        AppApplication.appDatabase!!.runInTransaction {
                            dataSearch.clear()
                            dataSearch.addAll(
                                contactDao!!.filterNotFriend(
                                    keySearch
                                )
                            )

                        }
                    }
                    postDelayed({
                        dataList.clear()
                        dataList.addAll(dataSearch)
                        adapterMyContact!!.notifyDataSetChanged()

                        binding.itemRcv.tvNew.text = String.format("%s %s", getString(R.string.not_friend), dataSearch.size.toString())

                    }, 500)
                } else {
                    val dataSearch = ArrayList<Friend>()
                    CoroutineScope(Dispatchers.IO).launch {
                        AppApplication.appDatabase!!.runInTransaction {
                            dataSearch.clear()
                            dataSearch.addAll(
                                contactDao!!.filterAll(
                                    keySearch
                                )
                            )


                        }
                    }
                    postDelayed({
                        binding.itemRcv.tvAll.text = String.format(
                            "%s %s", getString(R.string.all), dataSearch.size.toString()
                        )
                        dataList.clear()
                        dataList.addAll(dataSearch)
                        adapterMyContact!!.notifyDataSetChanged()
                    }, 500)
                }
            }, 1000)

        }
    }









    override fun clickAddFriend(id: String) {
//        callAddFriend(id)
    }

    override fun clickRecall(id: String) {
//        removeRequestFriend(id)
    }

    override fun clickFeedBack(id: String, position: Int) {
        positionItem = position
//        dialogFeedBack(id)
    }

    override fun clickMore(item: Friend, position: Int) {
        dialogMore = MoreDialog.Builder(this, item, 1).setListener(object : MoreDialog.OnListener {
            override fun onBlockUser(dialog: BaseDialog, idUser: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onUnFriendUser(dialog: BaseDialog, idUser: Int) {
                dataList[position].contactType = AppConstants.NOT_FRIEND
                adapterMyContact!!.notifyDataSetChanged()
            }

            override fun onContentReport(dialog: BaseDialog, type: Int, contentReport: String) {

            }


        })
        dialogMore!!.show()
    }

    override fun clickWall(position: Int) {
        if (dataList[position].userId == UserCache.getUser().id) {
            EventBus.getDefault().post(CurrentFragmentEventBus(AppConstants.FRAGMENT_PROFILE))
            EventBus.getDefault().post(FragmentEventBus(AppConstants.FRAGMENT_PROFILE))
            startActivity(HomeActivity::class.java)
        } else {
            try {
                val intent = Intent(
                    this, Class.forName(ModuleClassConstants.INFO_CUSTOMER)
                )
                val bundle = Bundle()
                bundle.putInt(
                    AccountConstants.ID, dataList[position].userId!!
                )
                keySearch = ""
                intent.putExtras(bundle)
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }
    /**
     * api tạo cuộc trò chuyện
     */
    private fun createGroup(position: Int) {
        EasyHttp.post(this)
            .api(CreateConversationApi.params(dataList[position].userId!!))
            .request(object : HttpCallback<HttpData<Conversation>>(this) {
                override fun onSucceed(data: HttpData<Conversation>) {
                    if (data.isRequestSucceed()) {
                        try {
                            val bundle = Bundle()
                            val intent = Intent(
                                this@PhoneBookActivity, Class.forName(ModuleClassConstants.CHAT_ACTIVITY)
                            )
                            val group = GroupChat()
                            group.type = AppConstants.TYPE_PRIVATE
                            group.id = data.getData()!!.conversationId
                            group.name = dataList[position].fullName!!
                            group.avatar.original.url = dataList[position].avatar
                            bundle.putString(AppConstants.GROUP_DATA, Gson().toJson(group))
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()
                        } catch (e: ClassNotFoundException) {
                            e.printStackTrace()
                        }
                    }

                }
            })
    }

    override fun clickMessage(position: Int) {
        createGroup(position)
    }

    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        toast("aaa")


    }

    /**
     * bỏ khoảng trắng trong số điện thoại
     */
    private fun formatPhone(text: String): String {
        var strPhone = ""
        val size = text.split(" ")
        for (i in 0 until text.split(" ").size)
            strPhone += size[i]

        return strPhone
    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun selected(btnOne: Int, btnTwo: Int) {
        if (btnOne == 1) {
            binding.itemRcv.tvAll.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_form_input_on)
            binding.itemRcv.tvAll.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemRcv.tvAll.setBackgroundResource(R.drawable.bg_form_not_friend)
            binding.itemRcv.tvAll.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.gray, null
                )
            )
        }
        if (btnTwo == 1) {
            binding.itemRcv.tvNew.setBackgroundResource(vn.hitu.ntb.R.drawable.bg_form_input_on)
            binding.itemRcv.tvNew.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.white, null
                )
            )
        } else {
            binding.itemRcv.tvNew.setBackgroundResource(R.drawable.bg_form_not_friend)
            binding.itemRcv.tvNew.setTextColor(
                resources.getColor(
                    vn.hitu.ntb.R.color.gray, null
                )
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged", "Range")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )
        contactList.clear()
        while (contacts!!.moveToNext()) {
            val ojb = ContactDevice()
            ojb.name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            ojb.phone = formatPhone(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))

            contactList.add(ojb)
        }

        contacts.close()

        getContact(contactList)

        postDelayed({
            if (btnAll == 1) {
                val dataSearch = ArrayList<Friend>()
                CoroutineScope(Dispatchers.IO).launch {
                    AppApplication.appDatabase!!.runInTransaction {
                        dataSearch.clear()
                        dataSearch.addAll(
                            contactDao!!.filterAll(
                                keySearch
                            )
                        )


                    }
                }
                postDelayed({
                    dataList.clear()
                    dataList.addAll(dataSearch)
                    adapterMyContact!!.notifyDataSetChanged()
                    binding.itemRcv.tvAll.text = String.format("%s %s", getString(R.string.all), dataSearch.size.toString())
                }, 500)
            } else {
                val dataSearch = ArrayList<Friend>()
                CoroutineScope(Dispatchers.IO).launch {
                    AppApplication.appDatabase!!.runInTransaction {
                        dataSearch.clear()
                        dataSearch.addAll(
                            contactDao!!.filterNotFriend(
                                keySearch
                            )
                        )
                    }
                }
                postDelayed({
                    dataList.clear()
                    dataList.addAll(dataSearch)
                    adapterMyContact!!.notifyDataSetChanged()

                    binding.itemRcv.tvNew.text = String.format("%s %s", getString(R.string.not_friend), dataSearch.size.toString())
                }, 500)

            }
            this.binding.srlContact.finishRefresh()
        }, 1000)
    }

    private fun checkEmpty(array: ArrayList<Friend>) {
        if (array.isEmpty()) {
            binding.itemRcv.recyclerViewMyFriend.visibility = View.GONE
            binding.itemRcv.lnEmpty.visibility = View.VISIBLE
        } else {
            binding.itemRcv.recyclerViewMyFriend.visibility = View.VISIBLE
            binding.itemRcv.lnEmpty.visibility = View.GONE
        }
    }


}