package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Fight(
    @PrimaryKey
    val id: Long,
    val name: String,
    val firstEnemyId: Long,
    val SecondEnemyId: Long)