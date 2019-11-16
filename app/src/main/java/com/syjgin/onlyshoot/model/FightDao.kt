package com.syjgin.onlyshoot.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FightDao {
    @Query("SELECT * FROM Fight ORDER BY date DESC")
    suspend fun getAll() : List<Fight>
    @Query("SELECT * FROM Fight WHERE id = :id")
    fun getById(id: Long) : Fight
    @Insert
    fun insert(fight: Fight)
    @Delete
    fun delete(fight: Fight)
}