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
    val usualArmorHead: Int,
    val proofArmorHead: Int,
    val usualArmorTorso: Int,
    val proofArmorTorso: Int,
    val usualArmorLeftHand: Int,
    val proofArmorLeftHand: Int,
    val usualArmorRightHand: Int,
    val proofArmorRightHand: Int,
    val usualArmorLeftLeg: Int,
    val proofArmorLeftLeg: Int,
    val usualArmorRightLeg: Int,
    val proofArmorRightLeg: Int,
    val damage: Int,
    val constantDamageModifier: Int,
    val tempDamageModifier: Int,
    val damageType: DamageType,
    val attackCount: Int,
    var hp: Int,
    val evasion: Int,
    val evasionCount: Int,
    val missPossibility: Int,
    val criticalHitAvoidance: Int,
    val criticalHitModifier: Int,
    val canUseRage: Boolean,
    val deathFromRage: Boolean,
    var squadId: Long = -1,
    val rage: Int = 10
) : Serializable