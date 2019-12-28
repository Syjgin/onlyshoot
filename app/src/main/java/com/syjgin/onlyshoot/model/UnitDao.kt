package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnitDao {
    @Query("SELECT * FROM SquadUnit WHERE id = :id")
    suspend fun getById(id: Long) : SquadUnit?

    @Query("SELECT * FROM SquadUnit WHERE parentId = :id")
    suspend fun getByArchetype(id: Long): List<SquadUnit>

    @Query("SELECT * FROM SquadUnit WHERE squadId = :id ORDER BY name")
    suspend fun getBySquad(id: Long) : List<SquadUnit>

    @Query("SELECT * FROM SquadUnit WHERE squadId = :id ORDER BY name")
    fun getBySquadLiveData(id: Long) : LiveData<List<SquadUnit>>

    @Query("SELECT * FROM SquadUnit WHERE squadId = :id AND name LIKE :nameFilter AND weaponId = :weaponFilter ORDER BY name")
    fun getBySquadLiveDataWithFilters(
        id: Long,
        nameFilter: String,
        weaponFilter: Long
    ): LiveData<List<SquadUnit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fight: SquadUnit)

    @Query("DELETE FROM SquadUnit WHERE id = :id")
    suspend fun delete(id: Long)
}