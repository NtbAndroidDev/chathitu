package vn.hitu.ntb.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @Author: Bùi Hửu Thắng
 * @Date: 17/12/2022
 */

@Entity(tableName = "contact_device")
class ContactDevice() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name = ""

    @ColumnInfo(name = "phone")
    @SerializedName("phone")
    var phone = ""

    @ColumnInfo(name = "is_new")
    @SerializedName("is_new")
    var isNew = false

    constructor(parcel: Parcel) : this() {
        uid = parcel.readInt()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        isNew = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeByte(if (isNew) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactDevice> {
        override fun createFromParcel(parcel: Parcel): ContactDevice {
            return ContactDevice(parcel)
        }

        override fun newArray(size: Int): Array<ContactDevice?> {
            return arrayOfNulls(size)
        }
    }

}
