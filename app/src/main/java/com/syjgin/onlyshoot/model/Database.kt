package com.syjgin.onlyshoot.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Fight::class, SquadUnit::class], version = 1, exportSchema = false)
@TypeConverters(DamageTypeConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun FightDao() : FightDao
    abstract fun UnitDao() : UnitDao
}