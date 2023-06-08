package vn.hitu.ntb.contact.ui.holder

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import vn.hitu.ntb.constants.ModuleClassConstants
import vn.hitu.ntb.contact.ui.adapter.FriendRequestAdapter
import vn.hitu.ntb.contact.ui.adapter.TypeFriendAdapter
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.AppUtils.hide
import vn.hitu.ntb.utils.AppUtils.show
import vn.techres.line.contact.databinding.ItemViewRequestFriendBinding

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 19/12/2022
 */
class HeaderRequestFriendHandle(
    private val binding: ItemViewRequestFriendBinding,
    private var requestFriendAdapter: FriendRequestAdapter,
    private var adapter: TypeFriendAdapter,
) {
    @SuppressLint("NotifyDataSetChanged")
    fun setData() {

        val numberRequest = adapter.getNumberRequestFriend()
        val numberMyFriend = adapter.getNumberMyFriend()

        AppUtils.initRecyclerViewHorizontal(binding.recyclerViewFriendRequest, requestFriendAdapter)
        binding.itemTitle.tvAmount.text = numberRequest.toString()
        binding.tvAll.text = String.format(
            "%s %s",
            adapter.getContext().getString(vn.hitu.ntb.R.string.all),
            numberMyFriend.toString()
        )


        if (numberRequest == 0) {
//            binding.recyclerViewFriendRequest.hide()
            binding.lnEmpty.show()
            binding.itemTitle.tvAmount.hide()
        } else {
//            binding.recyclerViewFriendRequest.show()
            binding.lnEmpty.hide()
            binding.itemTitle.tvAmount.show()

        }

        if (numberMyFriend == 0)
            binding.lnEmptyMyFriend.show()
        else
            binding.lnEmptyMyFriend.hide()

        binding.itemTitle.tvMoreAll.setOnClickListener {
            try {
                val intent = Intent(
                    requestFriendAdapter.getContext(),
                    Class.forName(ModuleClassConstants.REQUEST_FRIEND)
                )
                requestFriendAdapter.getContext().startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        binding.ivSync.setOnClickListener {
            try {
                val intent = Intent(
                    requestFriendAdapter.getContext(),
                    Class.forName(ModuleClassConstants.PHONE_BOOK_ACTIVITY)
                )
                requestFriendAdapter.getContext().startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        binding.lnSync.setOnClickListener {
            try {
                val intent = Intent(
                    requestFriendAdapter.getContext(),
                    Class.forName(ModuleClassConstants.PHONE_BOOK_ACTIVITY)
                )
                requestFriendAdapter.getContext().startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}