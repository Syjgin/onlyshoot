package com.syjgin.onlyshoot

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val d100 = 25
        val dozens = d100 / 10
        val units = d100 % 10
        val bodyPartIndex = units * 10 + dozens
        assertEquals(52, bodyPartIndex)
    }
}
