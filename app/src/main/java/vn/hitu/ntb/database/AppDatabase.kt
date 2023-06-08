package vn.hitu.ntb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import vn.hitu.ntb.model.entity.CategorySticker
import vn.hitu.ntb.model.entity.ContactDevice
import vn.hitu.ntb.model.entity.Friend

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 17/12/2022
 */
@Database(
    entities = [ Friend::class, ContactDevice::class, CategorySticker::class], version = 20, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao?
    abstract fun contactDeviceDao(): ContactDeviceDao?
    abstract fun stickerDao(): StickerDao?


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AloLineDatabase"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}