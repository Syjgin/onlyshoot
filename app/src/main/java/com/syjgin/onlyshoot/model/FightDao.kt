package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FightDao {
    @Query("SELECT * FROM Fight ORDER BY date DESC")
    fun getAll() : LiveData<List<Fight>>
    @Query("SELECT * FROM Fight WHERE id = :id")
    fun getById(id: Long) : Fight
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fight: Fight)
    @Delete
    fun delete(fight: Fight)
}