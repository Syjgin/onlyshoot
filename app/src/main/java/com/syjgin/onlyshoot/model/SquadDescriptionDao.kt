package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SquadDescriptionDao {
    @Query("SELECT * FROM SquadDescription")
    fun getAll() : LiveData<List<SquadDescription>>

    @Query("SELECT * FROM SquadDescription")
    suspend fun getAllSuspend(): List<SquadDescription>
    @Query("SELECT * FROM SquadDescription WHERE id = :id")
    suspend fun getById(id: Long) : SquadDescription?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(squad: SquadDescription)

    @Query("DELETE FROM SquadDescription WHERE id = :id")
    suspend fun delete(id: Long)
}