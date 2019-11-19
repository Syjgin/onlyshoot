package com.syjgin.onlyshoot.utils

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
}