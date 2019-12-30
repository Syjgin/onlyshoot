package com.syjgin.onlyshoot.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attack(
    val attackersGroupName: String,
    val attackersWeaponName: String,
    val defendersGroupName: String,
    val attackerIds: List<Long>,
    val defenderIds: List<Long>,
    val isRandom: Boolean,
    val count: Int
)