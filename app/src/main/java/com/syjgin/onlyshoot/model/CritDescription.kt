package com.syjgin.onlyshoot.model

import android.content.Context
import com.syjgin.onlyshoot.R

data class CritDescription(val description: String, val isDeath: Boolean) {
    companion object {
        fun generateCrit(
            context: Context,
            amount: Int,
            bodyPart: AttackResult.BodyPart,
            damageType: DamageType
        ): CritDescription {
            return when (bodyPart) {
                AttackResult.BodyPart.Head -> when (damageType) {
                    DamageType.Explosion -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.explode_head_1), false)
                        2 -> CritDescription(context.getString(R.string.explode_head_2), false)
                        3 -> CritDescription(context.getString(R.string.explode_head_3), false)
                        4 -> CritDescription(context.getString(R.string.explode_head_4), false)
                        5 -> CritDescription(context.getString(R.string.explode_head_5), false)
                        6 -> CritDescription(context.getString(R.string.explode_head_6), true)
                        7 -> CritDescription(context.getString(R.string.explode_head_7), true)
                        8 -> CritDescription(context.getString(R.string.explode_head_8), true)
                        9 -> CritDescription(context.getString(R.string.explode_head_9), true)
                        else -> CritDescription(context.getString(R.string.explode_head_10), true)
                    }
                    DamageType.Cut -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.rend_head_1), false)
                        2 -> CritDescription(context.getString(R.string.rend_head_2), false)
                        3 -> CritDescription(context.getString(R.string.rend_head_3), false)
                        4 -> CritDescription(context.getString(R.string.rend_head_4), false)
                        5 -> CritDescription(context.getString(R.string.rend_head_5), false)
                        6 -> CritDescription(context.getString(R.string.rend_head_6), false)
                        7 -> CritDescription(context.getString(R.string.rend_head_7), false)
                        8 -> CritDescription(context.getString(R.string.rend_head_8), true)
                        9 -> CritDescription(context.getString(R.string.rend_head_9), true)
                        else -> CritDescription(context.getString(R.string.rend_head_10), true)
                    }
                    DamageType.Strike -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.impact_head_1), false)
                        2 -> CritDescription(context.getString(R.string.impact_head_2), false)
                        3 -> CritDescription(context.getString(R.string.impact_head_3), false)
                        4 -> CritDescription(context.getString(R.string.impact_head_4), false)
                        5 -> CritDescription(context.getString(R.string.impact_head_5), false)
                        6 -> CritDescription(context.getString(R.string.impact_head_6), false)
                        7 -> CritDescription(context.getString(R.string.impact_head_7), false)
                        8 -> CritDescription(context.getString(R.string.impact_head_8), true)
                        9 -> CritDescription(context.getString(R.string.impact_head_9), true)
                        else -> CritDescription(context.getString(R.string.impact_head_10), true)
                    }
                    DamageType.Energy -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.energy_head_1), false)
                        2 -> CritDescription(context.getString(R.string.energy_head_2), false)
                        3 -> CritDescription(context.getString(R.string.energy_head_3), false)
                        4 -> CritDescription(context.getString(R.string.energy_head_4), false)
                        5 -> CritDescription(context.getString(R.string.energy_head_5), false)
                        6 -> CritDescription(context.getString(R.string.energy_head_6), false)
                        7 -> CritDescription(context.getString(R.string.energy_head_7), false)
                        8 -> CritDescription(context.getString(R.string.energy_head_8), true)
                        9 -> CritDescription(context.getString(R.string.energy_head_9), true)
                        else -> CritDescription(context.getString(R.string.energy_head_10), true)
                    }
                }
                AttackResult.BodyPart.Torso -> when (damageType) {
                    DamageType.Explosion -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.explode_body_1), false)
                        2 -> CritDescription(context.getString(R.string.explode_body_2), false)
                        3 -> CritDescription(context.getString(R.string.explode_body_3), false)
                        4 -> CritDescription(context.getString(R.string.explode_body_4), false)
                        5 -> CritDescription(context.getString(R.string.explode_body_5), false)
                        6 -> CritDescription(context.getString(R.string.explode_body_6), false)
                        7 -> CritDescription(context.getString(R.string.explode_body_7), false)
                        8 -> CritDescription(context.getString(R.string.explode_body_8), true)
                        9 -> CritDescription(context.getString(R.string.explode_body_9), true)
                        else -> CritDescription(context.getString(R.string.explode_body_10), true)
                    }
                    DamageType.Cut -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.rend_body_1), false)
                        2 -> CritDescription(context.getString(R.string.rend_body_2), false)
                        3 -> CritDescription(context.getString(R.string.rend_body_3), false)
                        4 -> CritDescription(context.getString(R.string.rend_body_4), false)
                        5 -> CritDescription(context.getString(R.string.rend_body_5), false)
                        6 -> CritDescription(context.getString(R.string.rend_body_6), false)
                        7 -> CritDescription(context.getString(R.string.rend_body_7), false)
                        8 -> CritDescription(context.getString(R.string.rend_body_8), false)
                        9 -> CritDescription(context.getString(R.string.rend_body_9), true)
                        else -> CritDescription(context.getString(R.string.rend_body_10), true)
                    }
                    DamageType.Strike -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.impact_body_1), false)
                        2 -> CritDescription(context.getString(R.string.impact_body_2), false)
                        3 -> CritDescription(context.getString(R.string.impact_body_3), false)
                        4 -> CritDescription(context.getString(R.string.impact_body_4), false)
                        5 -> CritDescription(context.getString(R.string.impact_body_5), false)
                        6 -> CritDescription(context.getString(R.string.impact_body_6), false)
                        7 -> CritDescription(context.getString(R.string.impact_body_7), false)
                        8 -> CritDescription(context.getString(R.string.impact_body_8), false)
                        9 -> CritDescription(context.getString(R.string.impact_body_9), true)
                        else -> CritDescription(context.getString(R.string.impact_body_10), true)
                    }
                    DamageType.Energy -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.energy_torso_1), false)
                        2 -> CritDescription(context.getString(R.string.energy_torso_2), false)
                        3 -> CritDescription(context.getString(R.string.energy_torso_3), false)
                        4 -> CritDescription(context.getString(R.string.energy_torso_4), false)
                        5 -> CritDescription(context.getString(R.string.energy_torso_5), false)
                        6 -> CritDescription(context.getString(R.string.energy_torso_6), false)
                        7 -> CritDescription(context.getString(R.string.energy_torso_7), false)
                        8 -> CritDescription(context.getString(R.string.energy_torso_8), false)
                        9 -> CritDescription(context.getString(R.string.energy_torso_9), true)
                        else -> CritDescription(context.getString(R.string.energy_torso_10), true)
                    }
                }
                AttackResult.BodyPart.RightHand, AttackResult.BodyPart.LeftHand -> when (damageType) {
                    DamageType.Explosion -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.explode_arm_1), false)
                        2 -> CritDescription(context.getString(R.string.explode_arm_2), false)
                        3 -> CritDescription(context.getString(R.string.explode_arm_3), false)
                        4 -> CritDescription(context.getString(R.string.explode_arm_4), false)
                        5 -> CritDescription(context.getString(R.string.explode_arm_5), false)
                        6 -> CritDescription(context.getString(R.string.explode_arm_6), false)
                        7 -> CritDescription(context.getString(R.string.explode_arm_7), false)
                        8 -> CritDescription(context.getString(R.string.explode_arm_8), true)
                        9 -> CritDescription(context.getString(R.string.explode_arm_9), true)
                        else -> CritDescription(context.getString(R.string.explode_arm_10), true)
                    }
                    DamageType.Cut -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.rend_arm_1), false)
                        2 -> CritDescription(context.getString(R.string.rend_arm_2), false)
                        3 -> CritDescription(context.getString(R.string.rend_arm_3), false)
                        4 -> CritDescription(context.getString(R.string.rend_arm_4), false)
                        5 -> CritDescription(context.getString(R.string.rend_arm_5), false)
                        6 -> CritDescription(context.getString(R.string.rend_arm_6), false)
                        7 -> CritDescription(context.getString(R.string.rend_arm_7), false)
                        8 -> CritDescription(context.getString(R.string.rend_arm_8), false)
                        9 -> CritDescription(context.getString(R.string.rend_arm_9), true)
                        else -> CritDescription(context.getString(R.string.rend_arm_10), true)
                    }
                    DamageType.Strike -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.impact_arm_1), false)
                        2 -> CritDescription(context.getString(R.string.impact_arm_2), false)
                        3 -> CritDescription(context.getString(R.string.impact_arm_3), false)
                        4 -> CritDescription(context.getString(R.string.impact_arm_4), false)
                        5 -> CritDescription(context.getString(R.string.impact_arm_5), false)
                        6 -> CritDescription(context.getString(R.string.impact_arm_6), false)
                        7 -> CritDescription(context.getString(R.string.impact_arm_7), false)
                        8 -> CritDescription(context.getString(R.string.impact_arm_8), false)
                        9 -> CritDescription(context.getString(R.string.impact_arm_9), true)
                        else -> CritDescription(context.getString(R.string.impact_arm_10), true)
                    }
                    DamageType.Energy -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.energy_arm_1), false)
                        2 -> CritDescription(context.getString(R.string.energy_arm_2), false)
                        3 -> CritDescription(context.getString(R.string.energy_arm_3), false)
                        4 -> CritDescription(context.getString(R.string.energy_arm_4), false)
                        5 -> CritDescription(context.getString(R.string.energy_arm_5), false)
                        6 -> CritDescription(context.getString(R.string.energy_arm_6), false)
                        7 -> CritDescription(context.getString(R.string.energy_arm_7), false)
                        8 -> CritDescription(context.getString(R.string.energy_arm_8), false)
                        9 -> CritDescription(context.getString(R.string.energy_arm_9), true)
                        else -> CritDescription(context.getString(R.string.energy_arm_10), true)
                    }
                }
                AttackResult.BodyPart.RightLeg, AttackResult.BodyPart.LeftLeg -> when (damageType) {
                    DamageType.Explosion -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.explode_leg_1), false)
                        2 -> CritDescription(context.getString(R.string.explode_leg_2), false)
                        3 -> CritDescription(context.getString(R.string.explode_leg_3), false)
                        4 -> CritDescription(context.getString(R.string.explode_leg_4), false)
                        5 -> CritDescription(context.getString(R.string.explode_leg_5), false)
                        6 -> CritDescription(context.getString(R.string.explode_leg_6), false)
                        7 -> CritDescription(context.getString(R.string.explode_leg_7), false)
                        8 -> CritDescription(context.getString(R.string.explode_leg_8), true)
                        9 -> CritDescription(context.getString(R.string.explode_leg_9), true)
                        else -> CritDescription(context.getString(R.string.explode_leg_10), true)
                    }
                    DamageType.Cut -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.rend_leg_1), false)
                        2 -> CritDescription(context.getString(R.string.rend_leg_2), false)
                        3 -> CritDescription(context.getString(R.string.rend_leg_3), false)
                        4 -> CritDescription(context.getString(R.string.rend_leg_4), false)
                        5 -> CritDescription(context.getString(R.string.rend_leg_5), false)
                        6 -> CritDescription(context.getString(R.string.rend_leg_6), false)
                        7 -> CritDescription(context.getString(R.string.rend_leg_7), false)
                        8 -> CritDescription(context.getString(R.string.rend_leg_8), false)
                        9 -> CritDescription(context.getString(R.string.rend_leg_9), true)
                        else -> CritDescription(context.getString(R.string.rend_leg_10), true)
                    }
                    DamageType.Strike -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.impact_leg_1), false)
                        2 -> CritDescription(context.getString(R.string.impact_leg_2), false)
                        3 -> CritDescription(context.getString(R.string.impact_leg_3), false)
                        4 -> CritDescription(context.getString(R.string.impact_leg_4), false)
                        5 -> CritDescription(context.getString(R.string.impact_leg_5), false)
                        6 -> CritDescription(context.getString(R.string.impact_leg_6), false)
                        7 -> CritDescription(context.getString(R.string.impact_leg_7), false)
                        8 -> CritDescription(context.getString(R.string.impact_leg_8), false)
                        9 -> CritDescription(context.getString(R.string.impact_leg_9), true)
                        else -> CritDescription(context.getString(R.string.impact_leg_10), true)
                    }
                    DamageType.Energy -> when (amount) {
                        1 -> CritDescription(context.getString(R.string.energy_leg_1), false)
                        2 -> CritDescription(context.getString(R.string.energy_leg_2), false)
                        3 -> CritDescription(context.getString(R.string.energy_leg_3), false)
                        4 -> CritDescription(context.getString(R.string.energy_leg_4), false)
                        5 -> CritDescription(context.getString(R.string.energy_leg_5), false)
                        6 -> CritDescription(context.getString(R.string.energy_leg_6), false)
                        7 -> CritDescription(context.getString(R.string.energy_leg_7), false)
                        8 -> CritDescription(context.getString(R.string.energy_leg_8), false)
                        9 -> CritDescription(context.getString(R.string.energy_leg_9), true)
                        else -> CritDescription(context.getString(R.string.energy_leg_10), true)
                    }
                }
            }
        }
    }
}