package com.syjgin.onlyshoot.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeaponDao {
    @Query("SELECT * FROM Weapon")
    fun getAll(): LiveData<List<Weapon>>

    @Query("SELECT * FROM Weapon WHERE id = :id")
    suspend fun getById(id: Long): Weapon

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weapon: Weapon)

    @Delete
    suspend fun delete(weapon: Weapon)
}