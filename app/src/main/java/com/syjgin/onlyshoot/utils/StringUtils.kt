package com.syjgin.onlyshoot.utils

import android.util.ArrayMap
import com.syjgin.onlyshoot.model.SquadUnit

object StringUtils {
    fun createDescription(units: List<SquadUnit>) : String {
        val result = StringBuilder()
        val map = ArrayMap<String, Int>()
        for(squadUnit in units) {
            if(map.contains(squadUnit.name)) {
                map[squadUnit.name] = map[squadUnit.name]!!+1
            } else {
                map[squadUnit.name] = 1
            }
        }
        for (keyValuePair in map.entries) {
            result.append(String.format("%s: %d", keyValuePair.key, keyValuePair.value))
        }
        return result.toString()
    }
}