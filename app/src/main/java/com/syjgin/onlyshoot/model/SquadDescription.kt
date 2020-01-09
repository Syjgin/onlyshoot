package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class SquadDescription(
    @PrimaryKey
    val id: Long,
    val name: String,
    val archetypeId: Long
) : Serializable