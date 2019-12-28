package com.syjgin.onlyshoot.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Fight::class, SquadUnit::class, SquadDescription::class, UnitArchetype::class, Weapon::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DamageTypeConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun fightDao() : FightDao
    abstract fun unitDao() : UnitDao
    abstract fun squadDescriptionDao() : SquadDescriptionDao
    abstract fun archetypeDao() : ArchetypeDao
    abstract fun weaponDao(): WeaponDao
}