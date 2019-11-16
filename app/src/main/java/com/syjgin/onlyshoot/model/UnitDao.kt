package com.syjgin.onlyshoot.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UnitDao {
    @Query("SELECT * FROM SquadUnit WHERE id = :id")
    suspend fun getById(id: Long) : SquadUnit
    @Query("SELECT * FROM SquadUnit WHERE squadId = :id")
    suspend fun getBySquad(id: Long) : List<SquadUnit>
    @Insert
    fun insert(fight: SquadUnit)
    @Delete
    fun delete(fight: SquadUnit)
}