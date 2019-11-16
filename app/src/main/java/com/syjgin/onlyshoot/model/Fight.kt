package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Fight(
    @PrimaryKey
    val id: Long,
    val name: String,
    val firstSquadId: Long,
    val secondSquadId: Long,
    val date: Long
    ) : Serializable