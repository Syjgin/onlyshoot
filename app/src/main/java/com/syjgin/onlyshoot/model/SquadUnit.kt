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
    val constantDamageModifier: Int,
    val tempDamageModifier: Int,
    val damageType: DamageType,
    var hp: Int,
    val evasion: Int,
    val evasionCount: Int,
    val criticalHitAvoidance: Int,
    val canUseRage: Boolean,
    val deathFromRage: Boolean,
    val weaponId: Long,
    var squadId: Long = -1,
    val rage: Int = 10
) : Serializable