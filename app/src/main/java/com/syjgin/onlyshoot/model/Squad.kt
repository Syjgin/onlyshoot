package com.syjgin.onlyshoot.model

data class Squad(val list: List<SquadUnit>, val isAttackers: Boolean, val name: String) {
    companion object {
        fun createFromUnitList(list: List<SquadUnit>, attackersId: Long, name: String) : Squad{
            return Squad(list, if(list.isEmpty()) false else list[0].squadId == attackersId, name)
        }
    }
}