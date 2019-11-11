package com.syjgin.onlyshoot.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Fight::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun FightDao() : FightDao
}