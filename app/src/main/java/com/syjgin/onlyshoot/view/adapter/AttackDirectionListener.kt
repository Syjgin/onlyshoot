package com.syjgin.onlyshoot.view.adapter

interface AttackDirectionListener {
    fun onAttackDirectionFinished(
        attackerId: Long,
        defenderId: Long,
        count: Int,
        color: Int
    )

    fun onRandomAttackCountIncreased(count: Int)
}