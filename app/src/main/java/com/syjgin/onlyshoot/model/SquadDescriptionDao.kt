package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SquadDescriptionDao {
    @Query("SELECT * FROM SquadDescription")
    fun getAll() : LiveData<List<SquadDescription>>
    @Query("SELECT * FROM SquadDescription WHERE id = :id")
    suspend fun getById(id: Long) : SquadDescription?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(squad: SquadDescription)
    @Delete
    suspend fun delete(squad: SquadDescription)
}