package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SquadArchetype(
    @PrimaryKey(autoGenerate = true) val id: Long, val squadId: Long,
    val unitArchetypeId: Long,
    var amount: Int,
    var name: String
)