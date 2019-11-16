package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class SquadUnit(
    @PrimaryKey
    var id: Long,
    val parentId: Long,
    val name: String,
    val attack: Int,
    val attackModifier: Int,
    val armorPenetration: Int,
    val usualArmor: Int,
    val proofArmor: Int,
    val damage: Int,
    val damageModifier: Int,
    val damageType: DamageType,
    val attackCount: Int,
    val hp: Int,
    val evasion: Int,
    val evasionCount: Int,
    val missPossibility: Int,
    val criticalHitAvoidance: Int,
    val criticalHitModifier: Int,
    val canUseRage: Boolean,
    val deathFromRage: Boolean,
    var squadId: Long = -1,
    val rage: Int = 10) : Serializable