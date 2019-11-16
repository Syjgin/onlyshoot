package com.syjgin.onlyshoot.model

import androidx.room.TypeConverter

class DamageTypeConverter {
    @TypeConverter
    fun getDamageType(num: Int) : DamageType {
        for(damageType in DamageType.values()) {
            if(damageType.ordinal == num) {
                return damageType
            }
        }
        return DamageType.Strike
    }

    @TypeConverter
    fun getDamageTypeInt(damageType: DamageType) : Int {
        return damageType.ordinal
    }
}