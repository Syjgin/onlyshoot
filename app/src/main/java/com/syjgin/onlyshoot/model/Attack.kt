package com.syjgin.onlyshoot.model

import android.os.Parcel
import android.os.Parcelable

data class Attack(
    val attackerId: Long,
    val defenderId: Long,
    val count: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(attackerId)
        parcel.writeLong(defenderId)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attack> {
        override fun createFromParcel(parcel: Parcel): Attack {
            return Attack(parcel)
        }

        override fun newArray(size: Int): Array<Attack?> {
            return arrayOfNulls(size)
        }
    }
}