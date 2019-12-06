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
    val usualArmorHands: Int,
    val proofArmorHands: Int,
    val usualArmorLegs: Int,
    val proofArmorLegs: Int,
    val damage: Int,
    val damageModifier: Int,
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
    val rage: Int = 10) : Serializable {
    companion object {
        fun equalsWithoutHP(squadUnit1: SquadUnit, squadUnit2: SquadUnit) : Boolean {
            if(squadUnit1.attack != squadUnit2.attack)
                return false
            if(squadUnit1.attackModifier != squadUnit2.attackModifier)
                return false
            if(squadUnit1.armorPenetration != squadUnit2.armorPenetration)
                return false
            if (squadUnit1.usualArmorHead != squadUnit2.usualArmorHead)
                return false
            if (squadUnit1.proofArmorHead != squadUnit2.proofArmorHead)
                return false
            if (squadUnit1.usualArmorTorso != squadUnit2.usualArmorTorso)
                return false
            if (squadUnit1.proofArmorTorso != squadUnit2.proofArmorTorso)
                return false
            if (squadUnit1.usualArmorHands != squadUnit2.usualArmorHands)
                return false
            if (squadUnit1.proofArmorHands != squadUnit2.proofArmorHands)
                return false
            if (squadUnit1.usualArmorLegs != squadUnit2.usualArmorLegs)
                return false
            if (squadUnit1.proofArmorLegs != squadUnit2.proofArmorLegs)
                return false
            if(squadUnit1.damage != squadUnit2.damage)
                return false
            if(squadUnit1.damageModifier != squadUnit2.damageModifier)
                return false
            if(squadUnit1.damageType != squadUnit2.damageType)
                return false
            if(squadUnit1.attackCount != squadUnit2.attackCount)
                return false
            if(squadUnit1.evasion != squadUnit2.evasion)
                return false
            if(squadUnit1.evasionCount != squadUnit2.evasionCount)
                return false
            if(squadUnit1.missPossibility != squadUnit2.missPossibility)
                return false
            if(squadUnit1.criticalHitAvoidance != squadUnit2.criticalHitAvoidance)
                return false
            if(squadUnit1.criticalHitModifier != squadUnit2.criticalHitModifier)
                return false
            if(squadUnit1.canUseRage != squadUnit2.canUseRage)
                return false
            if(squadUnit1.deathFromRage != squadUnit2.deathFromRage)
                return false
            if(squadUnit1.rage != squadUnit2.rage)
                return false
            return true
        }
    }
}