package vn.hitu.ntb.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import vn.hitu.ntb.model.entity.Friend

/**
 * @Author: NGUYEN THANH BINH
 * @Date: 12/12/2022
 */
@Dao
interface ContactDao  {

    //Insert an element
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(post: Friend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataAll(post: ArrayList<Friend>)

    //Select All Data
    @Query("SELECT * FROM contact")
    fun getAllData(): MutableList<Friend>

    //Check existence

    @Query("SELECT EXISTS (SELECT `user_id` FROM contact WHERE `user_id` = (:id))")
    fun checkData(id: String?): Boolean


    @Query("SELECT * FROM contact WHERE (`name` LIKE '%'||(:keySearch)||'%' OR `full_name` LIKE '%'||(:keySearch)||'%' OR `phone` LIKE '%'||(:keySearch)||'%')")
    fun filterAll(keySearch : String) : MutableList<Friend>

    @Query("SELECT * FROM contact WHERE (`name` LIKE '%'||(:keySearch)||'%' OR `full_name` LIKE '%'||(:keySearch)||'%' OR `phone` LIKE '%'||(:keySearch)||'%') AND `contact_type` <> 4  AND `contact_type` <> 0")
    fun filterNotFriend(keySearch : String) : MutableList<Friend>

    @Query("UPDATE contact SET contact_type = (:contactType) WHERE user_id = (:idUser) ")
    fun updateContactType(idUser : Int, contactType : Int)

    //Update an element
    @Update
    fun updateData(post: Friend)

    //Delete all
    @Query("DELETE FROM contact")
    fun deleteAllData()

    //Delete an element
    @Delete
    fun deleteData(post: Friend)


}