package com.syjgin.onlyshoot.view.adapter

interface AttackDirectionListener {
    fun onAttackDirectionFinished(
        attackerName: String,
        weaponName: String,
        defenderName: String,
        count: Int,
        color: Int
    )

    fun onRandomAttackCountIncreased(count: Int)
}