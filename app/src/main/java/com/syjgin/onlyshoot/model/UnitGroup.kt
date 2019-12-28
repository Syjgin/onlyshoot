package com.syjgin.onlyshoot.model

data class UnitGroup(
    val name: String,
    val weaponName: String,
    val weaponId: Long,
    val count: Int,
    val attackCount: Int,
    val archetypeId: Long
)