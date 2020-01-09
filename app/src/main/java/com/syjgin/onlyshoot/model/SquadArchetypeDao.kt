package com.syjgin.onlyshoot.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SquadArchetypeDao {
    @Query("SELECT DISTINCT squadId FROM SquadArchetype")
    suspend fun getIds(): List<Long>

    @Query("SELECT * FROM SquadArchetype WHERE squadId = :id")
    suspend fun getSquadArchetypeSync(id: Long): List<SquadArchetype>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(squadArchetype: SquadArchetype)

    @Query("DELETE FROM SquadArchetype WHERE id = :id")
    suspend fun delete(id: Long)
}