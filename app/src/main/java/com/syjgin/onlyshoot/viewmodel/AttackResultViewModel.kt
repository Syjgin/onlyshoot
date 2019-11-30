package com.syjgin.onlyshoot.viewmodel

import android.util.Log
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
                Log.d("ATTCKS", "--------------------------------------")
                Log.d("ATTCKS", "${attacker.name} -> ${defender.name}")
                Log.d("ATTCKS", "defender hp from db: ${defender.hp}")
                val d100 = created100()
                Log.d("ATTCKS", "d100: $d100")
                val fullAttack = attacker.attack + attacker.attackModifier
                Log.d("ATTCKS", "attack value: $fullAttack")
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
                    Log.d("ATTCKS", "attack failed: $result")
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
                        Log.d("ATTCKS", "same attack found, will be calculated later")
                        continue
                    } else {
                        defender = database.unitDao().getById(otherDefenderId)!!
                        Log.d("ATTCKS", "new defender: ${defender.name}")
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
                Log.d("ATTCKS", "reversed d100: $bodyPartIndex part: $firstAttackPart")
                val allParts = mutableListOf<AttackResult.BodyPart>()
                allParts.addAll(getAttackParts(firstAttackPart, attack.count))
                Log.d("ATTCKS", "all attack body parts: $allParts")
                var successAttackAmount = attack.count
                Log.d("ATTCKS", "attack count: $successAttackAmount")
                val evasionDice = created100()
                var successCount = (defender.evasion - evasionDice) / 10
                Log.d("ATTCKS", "evasion success count: $successCount")
                if (successCount > 0) {
                    evasions[defender.id] = evasions[defender.id]!! - successCount
                    Log.d(
                        "ATTCKS",
                        "remain evasions for ${defender.name}: ${evasions[defender.id]}"
                    )
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
                    Log.d("ATTCKS", "all attacks evaded")
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
                var totalArmor =
                    defender.usualArmor - attacker.armorPenetration + defender.proofArmor
                if (totalArmor < 0)
                    totalArmor = 0
                for (j in 0..successAttackAmount) {
                    var currentDamage = 0
                    for (k in 0..attacker.damage) {
                        currentDamage += random.nextInt(1, 11)
                    }
                    totalDamageWithoutArmor += (currentDamage + attacker.damageModifier)
                    totalDamage += (currentDamage + attacker.damageModifier - totalArmor)
                }
                Log.d("ATTCKS", "total armor: $totalArmor")
                Log.d("ATTCKS", "total damage without armor: $totalDamageWithoutArmor")
                Log.d("ATTCKS", "total damage: $totalDamage")
                if ((totalDamage >= attacker.rage || totalDamageWithoutArmor >= attacker.rage) && attacker.canUseRage) {
                    Log.d("ATTCKS", "rage calculation")
                    if (totalDamage >= attacker.rage) {
                        Log.d("ATTCKS", "can use rage")
                        defender.hp -= totalDamage
                        if (defender.deathFromRage) {
                            Log.d("ATTCKS", "death from rage")
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
                            Log.d("ATTCKS", "d5 crit: $d5")
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
                                Log.d("ATTCKS", "defender dead")
                                database.unitDao().delete(defender)
                            } else {
                                Log.d("ATTCKS", "defender hp: ${defender.hp}")
                                database.unitDao().insert(defender)
                            }
                            continue
                        }
                    } else if (totalDamageWithoutArmor >= attacker.rage) {
                        defender.hp -= 1
                        Log.d("ATTCKS", "rage with armor save")
                        if (defender.hp < 0) {
                            var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                            if (hpBeyound <= 0)
                                hpBeyound = 1
                            hpBeyound += attacker.criticalHitModifier
                            defender.hp -= attacker.criticalHitModifier
                            Log.d("ATTCKS", "crit: $hpBeyound")
                            val crit = CritDescription.generateCrit(
                                OnlyShootApp.getInstance().applicationContext,
                                hpBeyound,
                                allParts[0],
                                attacker.damageType
                            )
                            val result = AttackResult(
                                attacker.name,
                                defender.name,
                                1 + attacker.criticalHitModifier,
                                crit.description,
                                defender.hp,
                                if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                                allParts
                            )
                            results.add(result)
                            if (crit.isDeath) {
                                Log.d("ATTCKS", "defender dead")
                                database.unitDao().delete(defender)
                            } else {
                                Log.d("ATTCKS", "defender hp: ${defender.hp}")
                                database.unitDao().insert(defender)
                            }
                            continue
                        }
                    }
                } else if (totalDamage > 0) {
                    defender.hp -= totalDamage
                    if (defender.hp < 0) {
                        var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                        if (hpBeyound <= 0)
                            hpBeyound = 1
                        hpBeyound += attacker.criticalHitModifier
                        defender.hp -= attacker.criticalHitModifier
                        Log.d("ATTCKS", "crit: $hpBeyound")
                        val crit = CritDescription.generateCrit(
                            OnlyShootApp.getInstance().applicationContext,
                            hpBeyound,
                            allParts[0],
                            attacker.damageType
                        )
                        val result = AttackResult(
                            attacker.name,
                            defender.name,
                            1 + attacker.criticalHitModifier,
                            crit.description,
                            defender.hp,
                            if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                            allParts
                        )
                        results.add(result)
                        if (crit.isDeath) {
                            Log.d("ATTCKS", "defender dead")
                            database.unitDao().delete(defender)
                        } else {
                            Log.d("ATTCKS", "defender hp: ${defender.hp}")
                            database.unitDao().insert(defender)
                        }
                        continue
                    } else {
                        val result = AttackResult(
                            attacker.name,
                            defender.name,
                            totalDamage,
                            "",
                            defender.hp,
                            AttackResult.ResultState.Hit,
                            allParts
                        )
                        results.add(result)
                        Log.d("ATTCKS", "defender hp: ${defender.hp}")
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
                    Log.d("ATTCKS", "armor save")
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