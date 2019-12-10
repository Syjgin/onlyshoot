package com.syjgin.onlyshoot.model

data class Attack(
    val attackersGroupName: String,
    val defendersGroupName: String,
    val attackerIds: List<Long>,
    val defenderIds: List<Long>,
    val isRandom: Boolean,
    val count: Int
)