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
    val rage: Int = 10) : Serializable {
    fun convertToSquadUnit(squadId: Long, unitName: String) : SquadUnit {
        return SquadUnit(
            DbUtils.generateLongUUID(),
            id,
            unitName,
            attack,
            attackModifier,
            armorPenetration,
            usualArmor,
            proofArmor,
            damage,
            damageModifier,
            damageType,
            attackCount,
            hp,
            evasion,
            evasionCount,
            missPossibility,
            criticalHitAvoidance,
            criticalHitModifier,
            canUseRage,
            deathFromRage,
            squadId,
            rage
        )
    }
}