package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArchetypeDao {
    @Query("SELECT * FROM UnitArchetype WHERE id = :id")
    suspend fun getById(id: Long) : UnitArchetype?

    @Query("SELECT * FROM UnitArchetype")
    fun getAllBySquads() : LiveData<List<UnitArchetype>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fight: UnitArchetype)
    @Delete
    suspend fun delete(fight: UnitArchetype)
}