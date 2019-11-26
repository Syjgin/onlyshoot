package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UnitDao {
    @Query("SELECT * FROM SquadUnit WHERE id = :id")
    suspend fun getById(id: Long) : SquadUnit?

    @Query("SELECT * FROM SquadUnit WHERE squadId = :id ORDER BY name")
    suspend fun getBySquad(id: Long) : List<SquadUnit>

    @Query("SELECT * FROM SquadUnit WHERE squadId = :id ORDER BY name")
    fun getBySquadLiveData(id: Long) : LiveData<List<SquadUnit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fight: SquadUnit)
    @Delete
    suspend fun delete(fight: SquadUnit)
}