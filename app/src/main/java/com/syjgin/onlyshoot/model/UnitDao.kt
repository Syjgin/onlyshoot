package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UnitDao {
    @Query("SELECT * FROM SquadUnit WHERE id = :id")
    suspend fun getById(id: Long) : SquadUnit
    @Query("SELECT * FROM SquadUnit WHERE squadId = :id")
    suspend fun getBySquad(id: Long) : List<SquadUnit>
    @Query("SELECT * FROM SquadUnit WHERE squadId = :id")
    fun getBySquadLiveData(id: Long) : LiveData<List<SquadUnit>>

    @Query("SELECT * FROM SquadUnit WHERE squadId != -1 ORDER BY squadId")
    fun getAllBySquads() : LiveData<List<SquadUnit>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fight: SquadUnit)
    @Delete
    fun delete(fight: SquadUnit)
}