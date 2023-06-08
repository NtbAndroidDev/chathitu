package vn.hitu.ntb.interfaces

import vn.hitu.ntb.model.entity.Friend

/**
 * @Author: Phạm Văn Nhân
 * @Date: 05/10/2022
 */
interface MyFriendInterface {
    fun clickMore(item : Friend, position: Int)
    fun clickWall(position : Int)

    fun clickMessage(position: Int)
}