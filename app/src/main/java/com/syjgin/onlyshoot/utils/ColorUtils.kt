package com.syjgin.onlyshoot.utils

import android.graphics.Color
import kotlin.random.Random

object ColorUtils {
    private val random by lazy { Random(System.currentTimeMillis()) }
    fun randomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}