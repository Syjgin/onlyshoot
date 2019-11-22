package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FightDao {
    @Query("SELECT * FROM Fight ORDER BY date DESC")
    fun getAll() : LiveData<List<Fight>>
    @Query("SELECT * FROM Fight WHERE id = :id")
    suspend fun getById(id: Long) : Fight
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fight: Fight)
    @Delete
    suspend fun delete(fight: Fight)
}