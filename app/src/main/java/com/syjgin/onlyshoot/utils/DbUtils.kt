package com.syjgin.onlyshoot.utils

import com.syjgin.onlyshoot.model.Database
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.model.UnitGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

object DbUtils {
    const val NO_DATA = -1L
    fun generateLongUUID() : Long {
        var result: Long
        do {
            val uid = UUID.randomUUID()
            val buffer = ByteBuffer.wrap(ByteArray(16))
            buffer.putLong(uid.leastSignificantBits)
            buffer.putLong(uid.mostSignificantBits)
            val bigInt = BigInteger(buffer.array())
            result = bigInt.toLong()
        } while (result < 0)
        return result
    }

    fun getNextUnitName(targetName: String, existingNames: List<String>): String {
        var sameNameCount = 0
        val name2check = targetName.removeDigits()
        for (currentUnitName in existingNames) {
            val checkValue = currentUnitName.removeDigits()
            if (checkValue == name2check) {
                sameNameCount++
            }
        }
        return if (sameNameCount == 0) targetName else String.format(
            "%s %02d",
            targetName,
            sameNameCount + 1
        )
    }

    private fun String.removeDigits(): String {
        var result = this
        result = result.replace(Regex("\\d"), "")
        while (result.endsWith(" ")) {
            result = result.removeSuffix(" ")
        }
        return result
    }

    fun removeDigitsFrom(src: String): String {
        return src.removeDigits()
    }

    fun getGroupListBySquad(
        squad: List<SquadUnit>,
        database: Database,
        viewModelScope: CoroutineScope,
        groupsCallback: GroupsCallback
    ) {
        viewModelScope.launch {
            val unitMap = mutableMapOf<String, List<SquadUnit>>()
            for (squadUnit in squad) {
                val weapon = database.weaponDao().getById(squadUnit.weaponId)!!
                val removedDigitsName = "${squadUnit.name.removeDigits()} (${weapon.name})"
                if (!unitMap.containsKey(removedDigitsName)) {
                    unitMap[removedDigitsName] = listOf(squadUnit)
                } else {
                    val prevValue = unitMap[removedDigitsName]!!
                    val mutableSquad = mutableListOf<SquadUnit>()
                    mutableSquad.addAll(prevValue)
                    mutableSquad.add(squadUnit)
                    unitMap[removedDigitsName] = mutableSquad
                }
            }
            val result = mutableListOf<UnitGroup>()
            for (entry in unitMap.entries) {
                var attackCount = 0
                val weapon = database.weaponDao().getById(entry.value[0].weaponId)!!
                for (squadUnit in entry.value) {
                    attackCount += weapon.attackCount
                }
                val unitGroup =
                    UnitGroup(
                        entry.value[0].name.removeDigits(),
                        weapon.name,
                        weapon.id,
                        entry.value.size,
                        attackCount,
                        entry.value[0].parentId
                    )
                result.add(unitGroup)
            }
            groupsCallback.onGroupsCreationFinished(result)
        }
    }

    interface GroupsCallback {
        fun onGroupsCreationFinished(groups: List<UnitGroup>)
    }

    fun duplicateUnit(
        viewModelScope: CoroutineScope,
        database: Database,
        archetypeId: Long,
        squadId: Long,
        callback: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val targetUnit = database.archetypeDao().getById(archetypeId)
            val squad = database.unitDao().getBySquad(squadId)
            val names = mutableListOf<String>()
            for(currentUnit in squad) {
                names.add(currentUnit.name)
            }
            if(targetUnit != null) {
                val targetName = getNextUnitName(targetUnit.name, names)
                val newUnit = targetUnit.convertToSquadUnit(squadId, targetName)
                database.unitDao().insert(newUnit)
            }
            if (callback != null) {
                callback()
            }
        }
    }
}