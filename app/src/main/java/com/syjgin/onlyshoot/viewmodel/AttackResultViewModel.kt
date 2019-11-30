package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.AttackResult
import com.syjgin.onlyshoot.model.CritDescription
import kotlinx.coroutines.launch
import kotlin.random.Random

class AttackResultViewModel : BaseViewModel() {
    private val resultData = MutableLiveData<List<AttackResult>>()
    private val random = Random(System.currentTimeMillis())

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun load(attacks: List<Attack>, defendSquadId: Long) {
        viewModelScope.launch {
            val results = mutableListOf<AttackResult>()
            val defendersSquad = database.unitDao().getBySquad(defendSquadId)
            val defenderIds = defendersSquad.map { it.id }
            val mutableAttacks = mutableListOf<Attack>()
            mutableAttacks.addAll(attacks)
            val evasions = mutableMapOf<Long, Int>()
            for (defender in defendersSquad) {
                evasions[defender.id] = defender.evasionCount
            }
            for (i in mutableAttacks.indices) {
                val attack = mutableAttacks[i]
                val attacker = database.unitDao().getById(attack.attackerId)!!
                var defender = database.unitDao().getById(attack.defenderId)
                if (defender == null) {
                    continue
                }
                val d100 = created100()
                val fullAttack = attacker.attack + attacker.attackModifier
                if (d100 > fullAttack) {
                    val attackStatus = if (d100 > attacker.missPossibility) {
                        AttackResult.ResultState.Misfire
                    } else {
                        AttackResult.ResultState.Miss
                    }
                    val result = AttackResult(
                        attacker.name,
                        defender.name,
                        0,
                        "",
                        defender.hp,
                        attackStatus,
                        emptyList()
                    )
                    results.add(result)
                    continue
                }
                if (d100 == fullAttack) {
                    val otherDefenders = defenderIds.filter { it != defender!!.id }
                    val otherDefenderId = otherDefenders[random.nextInt(otherDefenders.size)]
                    val otherAttackWithSameId =
                        mutableAttacks.find { it.defenderId == otherDefenderId && it.attackerId == attack.attackerId }
                    if (otherAttackWithSameId != null) {
                        val newAttack = Attack(
                            otherAttackWithSameId.attackerId,
                            otherAttackWithSameId.defenderId,
                            otherAttackWithSameId.count + 1
                        )
                        mutableAttacks.remove(otherAttackWithSameId)
                        mutableAttacks.add(newAttack)
                        continue
                    } else {
                        defender = database.unitDao().getById(otherDefenderId)!!
                    }
                }
                val dozens = d100 / 10
                val units = d100 % 10
                val bodyPartIndex = units * 10 + dozens
                val firstAttackPart = when {
                    bodyPartIndex < 11 -> {
                        AttackResult.BodyPart.Head
                    }
                    bodyPartIndex < 21 -> {
                        AttackResult.BodyPart.RightHand
                    }
                    bodyPartIndex < 31 -> {
                        AttackResult.BodyPart.LeftHand
                    }
                    bodyPartIndex < 71 -> {
                        AttackResult.BodyPart.Torso
                    }
                    bodyPartIndex < 86 -> {
                        AttackResult.BodyPart.RightLeg
                    }
                    else -> {
                        AttackResult.BodyPart.LeftLeg
                    }
                }
                val allParts = mutableListOf<AttackResult.BodyPart>()
                allParts.addAll(getAttackParts(firstAttackPart, attack.count))
                var successAttackAmount = attack.count
                val evasionDice = created100()
                var successCount = (defender.evasion - evasionDice) / 10
                if (successCount > 0) {
                    evasions[defender.id] = evasions[defender.id]!! - successCount
                    if (evasions[defender.id]!! > 0) {
                        successCount += 1
                        successAttackAmount -= successCount
                        for (j in 0..successCount) {
                            if (allParts.isNotEmpty()) {
                                allParts.removeAt(0)
                            }
                        }
                    }
                }
                if (successAttackAmount == 0) {
                    val result = AttackResult(
                        attacker.name,
                        defender.name,
                        0,
                        "",
                        defender.hp,
                        AttackResult.ResultState.Evasion,
                        emptyList()
                    )
                    results.add(result)
                    continue
                }
                var totalDamage = 0
                var totalDamageWithoutArmor = 0
                for (j in 0..successAttackAmount) {
                    var totalArmor =
                        defender.usualArmor - attacker.armorPenetration + defender.proofArmor
                    if (totalArmor < 0)
                        totalArmor = 0
                    totalDamageWithoutArmor += (attacker.damage + attacker.damageModifier)
                    totalDamage += (attacker.damage + attacker.damageModifier - totalArmor)
                }
                if (totalDamage >= attacker.rage || totalDamageWithoutArmor >= attacker.rage) {
                    val canUseRage = attacker.canUseRage && totalDamage >= attacker.rage
                    if (canUseRage) {
                        defender.hp -= totalDamage
                        if (totalDamage > 0) {
                            if (defender.deathFromRage) {
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    totalDamage,
                                    "",
                                    defender.hp,
                                    AttackResult.ResultState.Death,
                                    allParts
                                )
                                results.add(result)
                                database.unitDao().delete(defender)
                                continue
                            } else {
                                val d5 = random.nextInt(1, 6)
                                defender.hp -= d5
                                val crit = CritDescription.generateCrit(
                                    OnlyShootApp.getInstance().applicationContext,
                                    d5,
                                    allParts[0],
                                    attacker.damageType
                                )
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    d5,
                                    crit.description,
                                    defender.hp,
                                    if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                                    allParts
                                )
                                results.add(result)
                                if (crit.isDeath) {
                                    database.unitDao().delete(defender)
                                } else {
                                    database.unitDao().insert(defender)
                                }
                                continue
                            }
                        }
                    } else if (attacker.canUseRage && totalDamageWithoutArmor >= attacker.rage) {
                        defender.hp -= 1
                        if (defender.hp < 0) {
                            var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                            if (hpBeyound <= 0)
                                hpBeyound = 1
                            val crit = CritDescription.generateCrit(
                                OnlyShootApp.getInstance().applicationContext,
                                hpBeyound,
                                allParts[0],
                                attacker.damageType
                            )
                            val result = AttackResult(
                                attacker.name,
                                defender.name,
                                1,
                                crit.description,
                                defender.hp,
                                if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                                allParts
                            )
                            results.add(result)
                            if (crit.isDeath) {
                                database.unitDao().delete(defender)
                            } else {
                                database.unitDao().insert(defender)
                            }
                            continue
                        } else {
                            val result = AttackResult(
                                attacker.name,
                                defender.name,
                                1,
                                "",
                                defender.hp,
                                AttackResult.ResultState.Hit,
                                allParts
                            )
                            results.add(result)
                            database.unitDao().insert(defender)
                            continue
                        }
                    }
                } else if (totalDamage > 0) {
                    defender.hp -= 1
                    if (defender.hp < 0) {
                        var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                        if (hpBeyound <= 0)
                            hpBeyound = 1
                        val crit = CritDescription.generateCrit(
                            OnlyShootApp.getInstance().applicationContext,
                            hpBeyound,
                            allParts[0],
                            attacker.damageType
                        )
                        val result = AttackResult(
                            attacker.name,
                            defender.name,
                            1,
                            crit.description,
                            defender.hp,
                            if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                            allParts
                        )
                        results.add(result)
                        if (crit.isDeath) {
                            database.unitDao().delete(defender)
                        } else {
                            database.unitDao().insert(defender)
                        }
                        continue
                    } else {
                        val result = AttackResult(
                            attacker.name,
                            defender.name,
                            1,
                            "",
                            defender.hp,
                            AttackResult.ResultState.Hit,
                            allParts
                        )
                        results.add(result)
                        database.unitDao().insert(defender)
                        continue
                    }
                } else {
                    val result = AttackResult(
                        attacker.name,
                        defender.name,
                        0,
                        "",
                        defender.hp,
                        AttackResult.ResultState.ArmorSave,
                        allParts
                    )
                    results.add(result)
                    continue
                }
            }
            resultData.postValue(results)
        }
    }

    private fun created100() = random.nextInt(1, 101)

    private fun getAttackParts(
        firstPart: AttackResult.BodyPart,
        count: Int
    ): List<AttackResult.BodyPart> {
        return when (count) {
            1 -> listOf(firstPart)
            2 -> when (firstPart) {
                AttackResult.BodyPart.Head -> listOf(firstPart, AttackResult.BodyPart.Head)
                AttackResult.BodyPart.Torso -> listOf(firstPart, AttackResult.BodyPart.Torso)
                AttackResult.BodyPart.RightHand -> listOf(firstPart, AttackResult.BodyPart.LeftHand)
                AttackResult.BodyPart.LeftHand -> listOf(firstPart, AttackResult.BodyPart.RightHand)
                AttackResult.BodyPart.RightLeg -> listOf(firstPart, AttackResult.BodyPart.LeftLeg)
                AttackResult.BodyPart.LeftLeg -> listOf(firstPart, AttackResult.BodyPart.RightLeg)
            }
            3 -> when (firstPart) {
                AttackResult.BodyPart.Head -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Head,
                    randomHand()
                )
                AttackResult.BodyPart.Torso -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Torso,
                    randomHand()
                )
                AttackResult.BodyPart.RightHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftHand,
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.LeftHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightHand,
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.RightLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftLeg,
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.LeftLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightLeg,
                    AttackResult.BodyPart.Torso
                )
            }
            4 -> when (firstPart) {
                AttackResult.BodyPart.Head -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Head,
                    randomHand(),
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.Torso -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head
                )
                AttackResult.BodyPart.RightHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftHand,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.Head
                )
                AttackResult.BodyPart.LeftHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightHand,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.Head
                )
                AttackResult.BodyPart.RightLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftLeg,
                    AttackResult.BodyPart.Torso,
                    randomHand()
                )
                AttackResult.BodyPart.LeftLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightLeg,
                    AttackResult.BodyPart.Torso,
                    randomHand()
                )
            }
            5 -> when (firstPart) {
                AttackResult.BodyPart.Head -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Head,
                    randomHand(),
                    AttackResult.BodyPart.Torso,
                    randomHand()
                )
                AttackResult.BodyPart.Torso -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head,
                    randomHand()
                )
                AttackResult.BodyPart.RightHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftHand,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.Head,
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.LeftHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightHand,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.Head,
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.RightLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftLeg,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head
                )
                AttackResult.BodyPart.LeftLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightLeg,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head
                )
            }
            6 -> when (firstPart) {
                AttackResult.BodyPart.Head -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Head,
                    randomHand(),
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.Torso -> listOf(
                    firstPart,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head,
                    randomHand(),
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.RightHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftHand,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.Head,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.RightHand
                )
                AttackResult.BodyPart.LeftHand -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightHand,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.Head,
                    AttackResult.BodyPart.Torso,
                    AttackResult.BodyPart.LeftHand
                )
                AttackResult.BodyPart.RightLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.LeftLeg,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head,
                    AttackResult.BodyPart.Torso
                )
                AttackResult.BodyPart.LeftLeg -> listOf(
                    firstPart,
                    AttackResult.BodyPart.RightLeg,
                    AttackResult.BodyPart.Torso,
                    randomHand(),
                    AttackResult.BodyPart.Head,
                    AttackResult.BodyPart.Torso
                )
            }
            else -> emptyList()
        }
    }

    private fun randomHand(): AttackResult.BodyPart {
        return if (random.nextBoolean()) AttackResult.BodyPart.LeftHand else AttackResult.BodyPart.RightHand
    }

    fun getResultLiveData(): LiveData<List<AttackResult>> {
        return resultData
    }
}