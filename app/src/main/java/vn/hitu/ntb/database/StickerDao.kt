package vn.hitu.ntb.database

import androidx.room.*
import vn.hitu.ntb.model.entity.CategorySticker

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 3/1/2023
 */
@Dao
interface StickerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(post: CategorySticker)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDataAll(post: ArrayList<CategorySticker>)

    //Select All Data
    @Query("SELECT * FROM sticker")
    fun getAllData(): MutableList<CategorySticker>

    @TypeConverters(CategorySticker::class)
    @Query("UPDATE sticker SET  `sticker` = (:sticker) WHERE  `category_sticker_id` = (:categoryStickerId)")
    fun updateType(categoryStickerId: String?, sticker: ArrayList<CategorySticker>)

    @Query("DELETE FROM sticker")
    fun deleteAllData()
}