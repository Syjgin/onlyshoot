package com.syjgin.onlyshoot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.AttackResult
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import kotlin.random.Random

class AttackResultViewModel : BaseViewModel() {
    private val resultData = MutableLiveData<List<AttackResult>>()
    private val logData = MutableLiveData<String>()
    private val random = Random(System.currentTimeMillis())
    private val log = StringBuilder()
    private val context = OnlyShootApp.getInstance().applicationContext

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    private fun log(message: String) {
        Log.d("ATTCKS", message)
        log.append("\n$message")
    }

    fun load(attacks: List<Attack>, defendSquadId: Long) {
        /*viewModelScope.launch {
            val random = Random(System.currentTimeMillis())
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
                    log("d100: $d100")
                    val fullAttack = attacker.attack + attacker.attackModifier
                    log(String.format(context.getString(R.string.attack_template), fullAttack))
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
                    log(String.format(context.getString(R.string.all_attack_parts), allParts))
                    var successAttackAmount = attacksBySingleUnit
                    log(
                        String.format(
                            context.getString(R.string.attack_count),
                            successAttackAmount
                        )
                    )
                    val evasionDice = created100()
                    var successCount = (defender.evasion - evasionDice) / 10
                    if (successCount > successAttackAmount) {
                        successCount = successAttackAmount
                    }
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
                        evasions[defender.id] = evasions[defender.id]!! - successCount
                        log(
                            String.format(
                                context.getString(R.string.remain_evasions),
                                defender.name,
                                evasions[defender.id]
                            )
                        )
                        if (evasions[defender.id]!! > 0) {
                            successCount += 1
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
                        log(String.format(context.getString(R.string.calculating_attack), j))
                        var currentDamage = 0
                        log(
                            String.format(
                                context.getString(R.string.damage_count),
                                attacker.damage
                            )
                        )
                        for (k in 0 until attacker.damage) {
                            currentDamage += random.nextInt(1, 11)
                        }
                        log(
                            String.format(
                                context.getString(R.string.current_damage),
                                j,
                                currentDamage + attacker.constantDamageModifier + attacker.tempDamageModifier
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
                        var totalArmor =
                            usualArmor - attacker.armorPenetration + proofArmor
                        if (totalArmor < 0)
                            totalArmor = 0
                        totalDamageWithoutArmor += (currentDamage + attacker.constantDamageModifier + attacker.tempDamageModifier)
                        totalDamage += (currentDamage + attacker.constantDamageModifier + attacker.tempDamageModifier - totalArmor)
                        log(
                            String.format(
                                context.getString(R.string.damage_for_attack),
                                j,
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
                    log(String.format(context.getString(R.string.total_damage), totalDamage))
                    if ((totalDamage >= attacker.rage || totalDamageWithoutArmor >= attacker.rage) && attacker.canUseRage) {
                        log(context.getString(R.string.rage_calculation))
                        if (totalDamage >= attacker.rage) {
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
                                log(String.format(context.getString(R.string.rage_crit), d5))
                                defender.hp -= d5
                                val crit = CritDescription.generateCrit(
                                    context,
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
                        } else if (totalDamageWithoutArmor >= attacker.rage) {
                            defender.hp -= (totalDamage + 1)
                            log(context.getString(R.string.rage_with_armor_save))
                            if (defender.hp < 0) {
                                var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                                if (hpBeyound <= 0)
                                    hpBeyound = 1
                                hpBeyound += attacker.criticalHitModifier
                                defender.hp -= attacker.criticalHitModifier
                                log(
                                    String.format(
                                        context.getString(R.string.crit_without_avoidance_log),
                                        defender.hp * -1
                                    )
                                )
                                log(
                                    String.format(
                                        context.getString(R.string.crit_modifier_log),
                                        attacker.criticalHitModifier
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
                                    attacker.damageType
                                )
                                val result = AttackResult(
                                    attacker.name,
                                    defender.name,
                                    totalDamage + 1 + attacker.criticalHitModifier,
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
                        defender.hp -= totalDamage
                        if (defender.hp < 0) {
                            var hpBeyound = (defender.hp * -1) - defender.criticalHitAvoidance
                            if (hpBeyound <= 0)
                                hpBeyound = 1
                            hpBeyound += attacker.criticalHitModifier
                            defender.hp -= attacker.criticalHitModifier
                            log(
                                String.format(
                                    context.getString(R.string.crit_without_avoidance_log),
                                    defender.hp * -1
                                )
                            )
                            log(
                                String.format(
                                    context.getString(R.string.crit_modifier_log),
                                    attacker.criticalHitModifier
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
                                attacker.damageType
                            )
                            val result = AttackResult(
                                attacker.name,
                                defender.name,
                                1 + attacker.criticalHitModifier,
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
        }*/
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