package com.syjgin.onlyshoot.model

import androidx.room.TypeConverter

class BurstTypeConverter {
    @TypeConverter
    fun getBurstType(num: Int): BurstType {
        for (burstType in BurstType.values()) {
            if (burstType.ordinal == num) {
                return burstType
            }
        }
        return BurstType.Single
    }

    @TypeConverter
    fun getBurstTypeInt(burstType: BurstType): Int {
        return burstType.ordinal
    }
}