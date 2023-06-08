package vn.hitu.ntb.chat.interfaces

import vn.hitu.ntb.model.entity.Friend


/**
 * @Author: NGUYEN THANH BINH
 * @Date: 23/12/2022
 */
interface ChooseItemListener {
    fun clickChooseItem(position: Int, isChecked : Boolean , item : Friend)
}