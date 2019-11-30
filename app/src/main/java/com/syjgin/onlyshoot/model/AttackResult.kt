package com.syjgin.onlyshoot.model

data class AttackResult(
    val attacker: String,
    val defender: String,
    val damage: Int,
    val description: String,
    val remainHP: Int,
    val resultState: ResultState,
    val affectedParts: List<BodyPart>
) {
    enum class ResultState {
        Hit,
        Misfire,
        Miss,
        Evasion,
        Death,
        ArmorSave
    }

    enum class BodyPart {
        Head,
        Torso,
        RightHand,
        LeftHand,
        RightLeg,
        LeftLeg
    }
}