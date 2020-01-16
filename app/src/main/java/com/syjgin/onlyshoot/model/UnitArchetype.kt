package com.syjgin.onlyshoot.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syjgin.onlyshoot.utils.DbUtils
import java.io.Serializable

@Entity
data class UnitArchetype(
    @PrimaryKey
    var id: Long,
    val name: String,
    val attack: Int,
    val attackModifier: Int,
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
    val hp: Int,
    val evasion: Int,
    val evasionCount: Int,
    val evasionModifier: Int,
    val criticalHitAvoidance: Int,
    val canUseRage: Boolean,
    val deathFromRage: Boolean,
    val weaponId: Long,
    val weaponName: String,
    val constantEnemyAttackModifier: Int = 0,
    val tempEnemyAttackModifier: Int = 0,
    val rage: Int = 10) : Serializable {
    fun convertToSquadUnit(squadId: Long, unitName: String) : SquadUnit {
        return SquadUnit(
            DbUtils.generateLongUUID(),
            id,
            unitName,
            attack,
            attackModifier,
            usualArmorHead,
            proofArmorHead,
            usualArmorTorso,
            proofArmorTorso,
            usualArmorLeftHand,
            proofArmorLeftHand,
            usualArmorRightHand,
            proofArmorRightHand,
            usualArmorLeftLeg,
            proofArmorLeftLeg,
            usualArmorRightLeg,
            proofArmorRightLeg,
            constantDamageModifier,
            tempDamageModifier,
            hp,
            evasion,
            evasionModifier,
            evasionCount,
            criticalHitAvoidance,
            canUseRage,
            deathFromRage,
            weaponId,
            weaponName,
            constantEnemyAttackModifier,
            tempEnemyAttackModifier,
            squadId,
            rage
        )
    }
}