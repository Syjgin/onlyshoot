package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weapon(
    @PrimaryKey
    val id: Long,
    val name: String,
    val attackModifier: Int,
    val damage: Int,
    val damageModifier: Int,
    val attackCount: Int,
    val missPossibility: Int,
    val criticalHitModifier: Int,
    val rage: Int,
    val damageType: DamageType,
    val armorPenetration: Int,
    val burstType: BurstType
)