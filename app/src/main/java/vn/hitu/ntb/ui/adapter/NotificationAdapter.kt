package vn.hitu.ntb.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.hitu.ntb.R
import vn.hitu.ntb.app.AppAdapter
import vn.hitu.ntb.constants.NotificationConstants
import vn.hitu.ntb.databinding.ItemNotificationBinding
import vn.hitu.ntb.interfaces.MoreListener
import vn.hitu.ntb.interfaces.NotificationsInterface
import vn.hitu.ntb.model.entity.Notification
import vn.hitu.ntb.utils.AppUtils
import vn.hitu.ntb.utils.TimeFormat

/**
 * @Author: HO QUANG TUNG
 * @Date: 02/01/2023
 */

class NotificationAdapter constructor(context: Context) : AppAdapter<Notification>(context) {
    private var moreListener: MoreListener? = null
    private var onNotifications: NotificationsInterface? = null
    fun setOnMore(moreListener: MoreListener) {
        this.moreListener = moreListener
    }

    fun setOnNotifications(onNotifications: NotificationsInterface) {
        this.onNotifications = onNotifications
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }


    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        AppViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBindView(position: Int) {
            val item = getItem(position)
            AppUtils.loadImageUser(binding.ivAvatar, item.avatar!!) // avatar
            binding.tvTime.text = TimeFormat.timeAgoString(getContext(), item.timestamp) //time

            val nameNotify = "<b> ${item.content}" + "</b> "
            binding.tvNotify.text = Html.fromHtml(nameNotify)

            binding.ivMore.setOnClickListener {
                moreListener?.onMore(item)
            }
            if (item.isViewed == 0) {
                binding.llBackgroundView.setBackgroundResource(R.color.orange_200)
            } else {
                binding.llBackgroundView.setBackgroundResource(R.color.white)
            }
            when (item.objectType) {

                // notification thanks
                NotificationConstants.ALOLINE_NOTIFICATION_THANKS -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_green_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_notification_thanks)

                }
                // reply comment
                NotificationConstants.ALOLINE_CUSTOMER_REPLY_COMMENT -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_green_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_fi_edit)

                }
                // reaction post
                NotificationConstants.ALOLINE_CUSTOMER_REACTION_POST -> {
                    when (item.reactionType) {
                        NotificationConstants.ALOLINE_REACTION_LOVE -> {
                            binding.llNotify.setBackgroundResource(R.drawable.ic_emotion_heart)
                      binding.ivNotify.visibility = View.GONE
                        }

                        NotificationConstants.ALOLINE_REACTION_HAHA -> {
                            binding.llNotify.setBackgroundResource(R.drawable.ic_emotion_exciting)
                            binding.ivNotify.visibility = View.GONE
                        }

                        NotificationConstants.ALOLINE_REACTION_SAD -> {
                            binding.llNotify.setBackgroundResource(R.drawable.ic_emotion_thrilled)
                       binding.ivNotify.visibility = View.GONE
                        }

                        NotificationConstants.ALOLINE_REACTION_VALUE -> {
                            binding.llNotify.setBackgroundResource(R.drawable.ic_emotion_value)
                            binding.ivNotify.visibility = View.GONE
                        }

                        NotificationConstants.ALOLINE_REACTION_ANGRY -> {
                            binding.llNotify.setBackgroundResource(R.drawable.ic_emotion_negative)
                            binding.ivNotify.visibility = View.GONE
                        }

                        NotificationConstants.ALOLINE_REACTION_NOTHING -> {
                            binding.llNotify.setBackgroundResource(R.drawable.ic_emotion_insipid)
                            binding.ivNotify.visibility = View.GONE
                        }
                    }


                }
                // reaction comment
                NotificationConstants.ALOLINE_CUSTOMER_REACTION_COMMENT -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_green_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_review_restaurant)

                }
                //nap tien vao tk
                NotificationConstants.ALOLINE_PAY_ACCOUNT -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_green_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_dollar)
                }
                // confirm report
                NotificationConstants.ALOLINE_CUSTOMER_ADMIN_CONFIRM_REPORT -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_red_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_notify_gift)


                }
                //cancel booking
                NotificationConstants.ALOLINE_CUSTOMER_CANCEL_BOOKING -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_red_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_booking_add)
                }
                //huy dang ky quản cáo
                NotificationConstants.ALOLINE_REFUSE_ADVERTISING_REGISTRATION -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_red_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_account_adv)
                }


                NotificationConstants.ALOLINE_PLUS_POINTS -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_orange_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_gem_point)

                }
                // create Comment
                NotificationConstants.ALOLINE_CUSTOMER_COMMENT -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_orange_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_share_value)

                }
                // create post
                NotificationConstants.ALOLINE_CUSTOMER_CREATE_POST -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_orange_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_share_value)

                }
                //conf booking
                NotificationConstants.ALOLINE_CUSTOMER_CONFIRM_BOOKING -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_orange_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_booking_add)

                }
                //xác nhận đăng ký tk
                NotificationConstants.ALOLINE_ACCEPT_ALOLINE_REGISTRATION -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_orange_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_account_adv)
                }
                // send request friend
                NotificationConstants.ALOLINE_CUSTOMER_REQUEST_FRIEND -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_blue_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_waiting_friend)

                }
                // accept friend
                NotificationConstants.ALOLINE_CUSTOMER_ACCEPT_REQUEST_FRIEND -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_blue_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_accept_friend)

                }
                //nhận quà
                NotificationConstants.ALOLINE_GIFT -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_blue_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_account_gift)
                }
                //đk thành viên
                NotificationConstants.ALOLINE_MEMBERSHIP_CARD -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_blue_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_notify_gift)
                }
                // gắn bill khách hàng
                NotificationConstants.ALOLINE_ATTCH_BILL_ORDER -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_blue_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_account_order)
                }
                //xác nhận đăng ký quảng cáo
                NotificationConstants.ALOLINE_ACCEPT_ADVERTISING_REGISTRATION -> {
                    binding.llNotify.setBackgroundResource(R.drawable.border_blue_50dp)
                    binding.ivNotify.setImageResource(R.drawable.ic_account_adv)
                }

            }
            // tất cả các thông báo liên quan đến branch review (comment, post, reaction, reply comment)
            itemView.setOnClickListener {
                itemView.isEnabled = false
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    binding.llBackgroundView.setBackgroundResource(R.color.white)
                    itemView.isEnabled = true
                    onNotifications?.markNotification(item, item.objectType!!)
                }, 300)

            }


        }
    }
}