package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SquadDescriptionDao {
    @Query("SELECT * FROM SquadDescription")
    fun getAll() : LiveData<List<SquadDescription>>
    @Query("SELECT * FROM SquadDescription WHERE id = :id")
    fun getById(id: Long) : SquadDescription
    @Insert
    fun insert(squad: SquadDescription)
    @Delete
    fun delete(squad: SquadDescription)
}