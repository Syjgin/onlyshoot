package com.syjgin.onlyshoot.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttackList(val attacks: List<Attack>)