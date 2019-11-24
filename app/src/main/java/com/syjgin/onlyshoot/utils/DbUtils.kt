package com.syjgin.onlyshoot.utils

import com.syjgin.onlyshoot.model.Database
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.model.UnitArchetype
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

    fun duplicateUnit(viewModelScope: CoroutineScope, database: Database, squadUnit: SquadUnit, squadId: Long) {
        viewModelScope.launch {
            val targetUnit = database.archetypeDao().getById(squadUnit.parentId)
            val squad = database.unitDao().getBySquad(squadId)
            var nameCount = 0
            for(currentUnit in squad) {
                if(currentUnit.name == squadUnit.name) {
                    nameCount++
                }
            }
            if(targetUnit != null) {
                val newUnit = targetUnit.convertToSquadUnit(squadId, squadUnit.name + nameCount)
                database.unitDao().insert(newUnit)
            }

        }
    }

    fun duplicateArchetype(
        viewModelScope: CoroutineScope,
        database: Database,
        archetype: UnitArchetype,
        squadId: Long
    ) {
        viewModelScope.launch {
            val squad = database.unitDao().getBySquad(squadId)
            var nameCount = 0
            for (currentUnit in squad) {
                if (currentUnit.name.startsWith(archetype.name)) {
                    nameCount++
                }
            }
            val newUnit = archetype.convertToSquadUnit(squadId, archetype.name + nameCount)
            database.unitDao().insert(newUnit)
        }
    }
}