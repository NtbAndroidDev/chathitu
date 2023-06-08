package vn.hitu.ntb.interfaces

import vn.hitu.ntb.model.entity.Notification

interface NotificationsInterface {
    fun markNotification(notifications: Notification, objectType: Int)
    fun onAction(notifications: Notification)
    fun onAcceptFriend(notifications: Notification)
    fun onRefuseFriend(notifications: Notification)
}