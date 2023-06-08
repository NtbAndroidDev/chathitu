package vn.hitu.ntb.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.hitu.ntb.model.entity.ContactDevice
import vn.hitu.ntb.model.entity.Friend

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 17/12/2022
 */
@Dao
interface ContactDeviceDao {
    //Insert an element
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(post: ContactDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataAll(post: ArrayList<ContactDevice>)

    //Select All Data
    @Query("SELECT * FROM contact_device")
    fun getAllData(): MutableList<ContactDevice>

    @Query("SELECT is_new FROM contact_device WHERE `phone` = (:phone)")
    fun checkIsNew(phone: String?): Int

    //Delete all
    @Query("DELETE FROM contact_device")
    fun deleteAllData()

    //Delete an element
    @Delete
    fun deleteData(post: Friend)
}