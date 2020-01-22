package com.syjgin.onlyshoot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.*
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import kotlinx.coroutines.launch
import kotlin.random.Random

class AttackResultViewModel : BaseViewModel() {
    private val resultData = MutableLiveData<List<AttackResult>>()
    private val logData = MutableLiveData<String>()
    private val log = StringBuilder()
    private var random = Random(System.currentTimeMillis())
    private val context = OnlyShootApp.getInstance().applicationContext
    private var alreadyCalculated = false

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    private fun log(message: String) {
        Log.d("ATTCKS", message)
        log.append("\n$message")
    }

    fun load(attacks: List<Attack>, defendSquadId: Long) {
        if (alreadyCalculated)
            return
        viewModelScope.launch {
            val results = mutableListOf<AttackResult>()
            val mutableAttacks = mutableListOf<Attack>()
            mutableAttacks.addAll(attacks)
            val evasions = mutableMapOf<Long, Int>()
            val defendersSquadInitialState = database.unitDao().getBySquad(defendSquadId)
            for (defender in defendersSquadInitialState) {
                evasions[defender.id] = defender.evasionCount
            }
            for (i in mutableAttacks.indices) {
                val attack = mutableAttacks[i]
                val attacksBySingleUnit = attack.count / attack.attackerIds.size
                for (attackerId in attack.attackerIds) {
                    val currentDefender = if (attack.isRandom) {
                        attack.defenderIds[random.nextInt(attack.defenderIds.size)]
                    } else {
                        val defendersSquad = database.unitDao().getBySquad(defendSquadId)
                        var randomDefender: SquadUnit? = null
                        var minHP = Int.MAX_VALUE
                        var result = attack.defenderIds[random.nextInt(attack.defenderIds.size)]
                        while (randomDefender == null) {
                            randomDefender = database.unitDao().getById(result)
                            if (randomDefender != null) {
                                minHP = randomDefender.hp
                            } else {
                                result = attack.defenderIds[random.nextInt(attack.defenderIds.size)]
                            }
                        }
                        for (defender in defendersSquad) {
                            if (attack.defenderIds.contains(defender.id)) {
                                if (defender.hp < minHP) {
                                    minHP = defender.hp
                                    result = defender.id
                                }
                            }
                        }
                        result
                    }
                    log("--------------------------------------")
                    val attacker = database.unitDao().getById(attackerId)!!
                    var defender = database.unitDao().getById(currentDefender)
                    if (defender == null) {
                        val defenderDescription =
                            defendersSquadInitialState.findLast { it.id == currentDefender }!!
                        log(context.getString(R.string.already_dead))
                        val result = AttackResult(
                            attacker.name,
                            defenderDescription.name,
                            0,
                            context.getString(R.string.already_dead),
                            0,
                            AttackResult.ResultState.Death,
                            emptyList(),
                            attacksBySingleUnit
                        )
                        results.add(result)
                        continue
                    }
                    log("${attacker.name} -> ${defender.name}")
                    val d100 = created100()
                    log(String.format(context.getString(R.string.attack_dice), d100))
                    val weapon = database.weaponDao().getById(attacker.weaponId)!!
                    val fullAttack =
                        attacker.attack + attacker.attackModifier + weapon.attackModifier + defender.tempEnemyAttackModifier + defender.constantEnemyAttackModifier
                    val attackSuccessCount =
                        Math.min(
                            (if (d100 < fullAttack) 1 else 0) + (fullAttack - d100) / 10,
                            attacksBySingleUnit
                        )
                    log(
                        String.format(
                            context.getString(R.string.attack_success_count),
                            if (attackSuccessCount > 0) attackSuccessCount else 0
                        )
                    )
                    var successAttackAmount = when (weapon.burstType) {
                        BurstType.Single -> if (attackSuccessCount > 0) 1 else 0
                        BurstType.Short -> if (attackSuccessCount > 0) {
                            1 + attackSuccessCount / 2
                        } else {
                            0
                        }
                        BurstType.Long -> attackSuccessCount
                    }
                    log(String.format(context.getString(R.string.burst_count), successAttackAmount))
                    log(String.format(context.getString(R.string.attack_log_template), fullAttack))
                    if (d100 > fullAttack) {
                        val attackStatus = if (d100 > weapon.missPossibility) {
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
                            emptyList(),
                            attacksBySingleUnit
                        )
                        log(String.format(context.getString(R.string.attack_failed), fullAttack))
                        results.add(result)
                        continue
                    }
                    if (d100 == fullAttack) {
                        val defenderSquadIds =
                            database.unitDao().getBySquad(defendSquadId).map { it.id }
                        val otherDefenders = defenderSquadIds.filter { it != defender!!.id }
                        val otherDefenderId = otherDefenders[random.nextInt(otherDefenders.size)]
                        defender = database.unitDao().getById(otherDefenderId)!!
                        log(String.format(context.getString(R.string.new_defender), defender.name))
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
                    log(
                        String.format(
                            context.getString(R.string.attack_part),
                            bodyPartIndex,
                            firstAttackPart.toString()
                        )
                    )
                    val allParts = mutableListOf<AttackResult.BodyPart>()
                    allParts.addAll(getAttackParts(firstAttackPart, attacksBySingleUnit))
                    log(
                        String.format(
                            context.getString(R.string.attack_count),
                            successAttackAmount
                        )
                    )
                    if (evasions[defender.id]!! > 0) {
                        val evasionDice = created100()
                        log(String.format(context.getString(R.string.evasion_dice), evasionDice))
                        val fullEvasion = defender.evasion + defender.evasionModifier
                        var successCount =
                            (if (evasionDice < fullEvasion) 1 else 0) + (fullEvasion - evasionDice) / 10
                        if (successCount > successAttackAmount) {
                            successCount = successAttackAmount
                        }
                        log(
                            String.format(
                                context.getString(R.string.evasion_log_template),
                                fullEvasion
                            )
                        )
                        log(
                            String.format(
                                context.getString(R.string.previous_evasion),
                                evasions[defender.id]
                            )
                        )
                        if (successCount > 0) {
                            log(
                                String.format(
                                    context.getString(R.string.evasion_success),
                                    successCount
                                )
                            )
                            successAttackAmount -= successCount
                            log(
                                String.format(
                                    context.getString(R.string.attacks_after_evasion),
                                    successAttackAmount
                                )
                            )
                            for (j in 0..successCount) {
                                if (allParts.isNotEmpty()) {
                                    allParts.removeAt(0)
                                }
                            }
                        }
                    }
                    if (evasions[defender.id]!! > 0)
                        evasions[defender.id] = evasions[defender.id]!! - 1
                    log(
                        String.format(
                            context.getString(R.string.remain_evasions),
                            defender.name,
                            evasions[defender.id]
                        )
                    )
                    if (successAttackAmount <= 0) {
                        log(context.getString(R.string.all_attacks_evaded))
                        val result = AttackResult(
                            attacker.name,
                            defender.name,
                            0,
                            "",
                            defender.hp,
                            AttackResult.ResultState.Evasion,
                            emptyList(),
                            attacksBySingleUnit
                        )
                        results.add(result)
                        continue
                    }
                    var totalDamage = 0
                    var totalDamageWithoutArmor = 0
                    for (j in 0 until successAttackAmount) {
                        log(String.format(context.getString(R.string.calculating_attack), j + 1))
                        var currentDamage = 0
                        val damage = weapon.damage
                        log(
                            String.format(
                                context.getString(R.string.damage_count),
                                damage
                            )
                        )
                        for (k in 0 until damage) {
                            currentDamage += random.nextInt(1, 11)
                        }
                        log(
                            String.format(
                                context.getString(R.string.damage_d10),
                                j + 1, currentDamage
                            )
                        )
                        log(
                            String.format(
                                context.getString(R.string.current_damage),
                                j + 1,
                                currentDamage + attacker.constantDamageModifier + attacker.tempDamageModifier + weapon.damageModifier
                            )
                        )
                        val currentPart =
                            if (j < allParts.size) allParts[j] else allParts[allParts.size - 1]
                        val usualArmor = when (currentPart) {
                            AttackResult.BodyPart.Head -> defender.usualArmorHead
                            AttackResult.BodyPart.Torso -> defender.usualArmorTorso
                            AttackResult.BodyPart.RightHand -> defender.usualArmorRightHand
                            AttackResult.BodyPart.LeftHand -> defender.usualArmorLeftHand
                            AttackResult.BodyPart.RightLeg -> defender.usualArmorRightLeg
                            AttackResult.BodyPart.LeftLeg -> defender.usualArmorLeftLeg
                        }
                        val proofArmor = when (currentPart) {
                            AttackResult.BodyPart.Head -> defender.proofArmorHead
                            AttackResult.BodyPart.Torso -> defender.proofArmorTorso
                            AttackResult.BodyPart.RightHand -> defender.proofArmorRightHand
                            AttackResult.BodyPart.LeftHand -> defender.proofArmorLeftHand
                            AttackResult.BodyPart.RightLeg -> defender.proofArmorRightLeg
                            AttackResult.BodyPart.LeftLeg -> defender.proofArmorLeftLeg
                        }
                        var currentPenetration = 0
                        for (k in 0..weapon.armorPenetration) {
                            currentPenetration += random.nextInt(0, 11)
                        }
                        log(
                            String.format(
                                context.getString(R.string.armor_penetration_log),
                                currentPenetration,
                                weapon.armorPenetrationModifier
                            )
                        )
                        currentPenetration += weapon.armorPenetrationModifier
                        var totalArmor =
                            usualArmor - currentPenetration
                        if (totalArmor < 0)
                            totalArmor = 0
                        totalArmor += proofArmor
                        totalDamageWithoutArmor += (currentDamage + attacker.constantDamageModifier + attacker.tempDamageModifier + weapon.damageModifier)
                        totalDamage += (currentDamage + attacker.constantDamageModifier + attacker.tempDamageModifier + weapon.damageModifier - totalArmor)
                        log(
                            String.format(
                                context.getString(R.string.damage_for_attack),
                                j + 1,
                                totalDamage
                            )
                        )
                        if (j < allParts.size) {
                            log(
                                String.format(
                                    context.getString(R.string.armor_for_attack),
                                    allParts[j].toString(),
                                    totalArmor
                                )
                            )
                        }
                    }
                    log(
                        String.format(
                            context.getString(R.string.total_damage_without_armor),
                            totalDamageWithoutArmor
                        )
                    )
                    if (totalDamage > 0) {
                        log(String.format(context.getString(R.string.total_damage), totalDamage))
                    }
                    val rage = if (attacker.rage > weapon.rage) attacker.rage else weapon.rage
                    if ((totalDamage >= rage || totalDamageWithoutArmor >= rage) && attacker.canUseRage) {
                        log(context.getString(R.string.rage_calculation))
                        if (totalDamage >= rage) {
                            log(
                                String.format(
                                    context.getString(R.string.hp_before_attack),
                                    defender.hp
                                )
                            )
                            defender.hp -= totalDamage
                            if (defender.deathFromRage) {
                                log(context.getString(R.string.death_from_rage))
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    totalDamage,
                                    "",
                                    defender.hp,
                                    AttackResult.ResultState.Death,
                                    allParts,
                                    attacksBySingleUnit
                                )
                                results.add(result)
                                database.unitDao().delete(defender.id)
                                continue
                            } else {
                                val d5 = random.nextInt(1, 6)
                                log(
                                    String.format(
                                        context.getString(R.string.hp_before_attack),
                                        defender.hp
                                    )
                                )
                                log(String.format(context.getString(R.string.rage_crit), d5))
                                defender.hp -= d5
                                val crit = CritDescription.generateCrit(
                                    context,
                                    d5,
                                    allParts[0],
                                    weapon.damageType
                                )
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    d5,
                                    crit.description,
                                    defender.hp,
                                    if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                                    allParts,
                                    attacksBySingleUnit
                                )
                                results.add(result)
                                if (crit.isDeath) {
                                    log(context.getString(R.string.defender_dead))
                                    database.unitDao().delete(defender.id)
                                } else {
                                    log(
                                        String.format(
                                            context.getString(R.string.defender_hp),
                                            defender.hp
                                        )
                                    )
                                    database.unitDao().insert(defender)
                                }
                                continue
                            }
                        } else if (totalDamageWithoutArmor >= rage) {
                            log(
                                String.format(
                                    context.getString(R.string.hp_before_attack),
                                    defender.hp
                                )
                            )
                            defender.hp -= (totalDamage + 1)
                            log(context.getString(R.string.rage_with_armor_save))
                            if (defender.hp < 0) {
                                var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                                if (hpBeyound <= 0)
                                    hpBeyound = 1
                                hpBeyound += weapon.criticalHitModifier
                                defender.hp -= weapon.criticalHitModifier
                                log(
                                    String.format(
                                        context.getString(R.string.crit_without_avoidance_log),
                                        defender.hp * -1
                                    )
                                )
                                defender.hp = hpBeyound * -1
                                log(
                                    String.format(
                                        context.getString(R.string.crit_modifier_log),
                                        weapon.criticalHitModifier
                                    )
                                )
                                log(
                                    String.format(
                                        context.getString(R.string.crit_avoidance_log),
                                        defender.criticalHitAvoidance
                                    )
                                )
                                log(String.format(context.getString(R.string.crit), hpBeyound))
                                val crit = CritDescription.generateCrit(
                                    context,
                                    hpBeyound,
                                    allParts[0],
                                    weapon.damageType
                                )
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    totalDamage + 1 + weapon.criticalHitModifier,
                                    crit.description,
                                    defender.hp,
                                    if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                                    allParts,
                                    attacksBySingleUnit
                                )
                                results.add(result)
                                if (crit.isDeath) {
                                    log(context.getString(R.string.defender_dead))
                                    database.unitDao().delete(defender.id)
                                } else {
                                    log(
                                        String.format(
                                            context.getString(R.string.defender_hp),
                                            defender.hp
                                        )
                                    )
                                    database.unitDao().insert(defender)
                                }
                                continue
                            } else {
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    totalDamage + 1,
                                    "",
                                    defender.hp,
                                    AttackResult.ResultState.Hit,
                                    allParts,
                                    attacksBySingleUnit
                                )
                                results.add(result)
                                log(
                                    String.format(
                                        context.getString(R.string.defender_hp),
                                        defender.hp
                                    )
                                )
                                database.unitDao().insert(defender)
                                continue
                            }
                        }
                    } else if (totalDamage > 0) {
                        log(
                            String.format(
                                context.getString(R.string.hp_before_attack),
                                defender.hp
                            )
                        )
                        defender.hp -= totalDamage
                        if (defender.hp < 0) {
                            var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                            if (hpBeyound <= 0)
                                hpBeyound = 1
                            hpBeyound += weapon.criticalHitModifier
                            defender.hp -= weapon.criticalHitModifier
                            log(
                                String.format(
                                    context.getString(R.string.crit_without_avoidance_log),
                                    defender.hp * -1
                                )
                            )
                            defender.hp = hpBeyound * -1
                            log(
                                String.format(
                                    context.getString(R.string.crit_modifier_log),
                                    weapon.criticalHitModifier
                                )
                            )
                            log(
                                String.format(
                                    context.getString(R.string.crit_avoidance_log),
                                    defender.criticalHitAvoidance
                                )
                            )
                            log(String.format(context.getString(R.string.crit), hpBeyound))
                            val crit = CritDescription.generateCrit(
                                context,
                                hpBeyound,
                                allParts[0],
                                weapon.damageType
                            )
                            val result = AttackResult(
                                attacker.name,
                                defender.name,
                                1 + weapon.criticalHitModifier,
                                crit.description,
                                defender.hp,
                                if (crit.isDeath) AttackResult.ResultState.Death else AttackResult.ResultState.Hit,
                                allParts,
                                attacksBySingleUnit
                            )
                            results.add(result)
                            if (crit.isDeath) {
                                log(context.getString(R.string.defender_dead))
                                database.unitDao().delete(defender.id)
                            } else {
                                log(
                                    String.format(
                                        context.getString(R.string.defender_hp),
                                        defender.hp
                                    )
                                )
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
                                allParts,
                                attacksBySingleUnit
                            )
                            results.add(result)
                            log(String.format(context.getString(R.string.defender_hp), defender.hp))
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
                            allParts,
                            attacksBySingleUnit
                        )
                        log(context.getString(R.string.armor_save))
                        results.add(result)
                        continue
                    }
                }
            }
            resultData.postValue(results)
            logData.postValue(log.toString())
        }
        alreadyCalculated = true
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
            else -> when (firstPart) {
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
        }
    }

    private fun randomHand(): AttackResult.BodyPart {
        return if (random.nextBoolean()) AttackResult.BodyPart.LeftHand else AttackResult.BodyPart.RightHand
    }

    fun getResultLiveData(): LiveData<List<AttackResult>> {
        return resultData
    }

    fun getLogLiveData(): LiveData<String> {
        return logData
    }

    override fun goBack() {
        router.backTo(OnlyShootScreen(ScreenEnum.AddEditFight))
    }
}