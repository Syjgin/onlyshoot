package com.syjgin.onlyshoot.utils

import com.syjgin.onlyshoot.model.Database
import com.syjgin.onlyshoot.model.SquadUnit
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
        val name2check = targetName.replace(Regex("\\d"), "").replace(" ", "")
        for (currentUnitName in existingNames) {
            val checkValue = currentUnitName.replace(Regex("\\d"), "").replace(" ", "")
            if (checkValue == name2check) {
                sameNameCount++
            }
        }
        return if (sameNameCount == 0) targetName else String.format(
            "%s %d",
            targetName,
            sameNameCount + 1
        )
    }

    fun duplicateUnit(
        viewModelScope: CoroutineScope,
        database: Database,
        squadUnit: SquadUnit,
        squadId: Long,
        callback: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val targetUnit = database.archetypeDao().getById(squadUnit.parentId)
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