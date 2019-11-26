package com.syjgin.onlyshoot.model

data class Squad(val list: List<SquadUnit>, val isAttackers: Boolean, val name: String) {
    companion object {
        fun createFromUnitList(list: List<SquadUnit>, attackersId: Long, name: String) : Squad{
            return Squad(list, if(list.isEmpty()) false else list[0].squadId == attackersId, name)
        }
    }

    fun isMembersIdentical() : Boolean {
        for(i in list.indices) {
            if(i < (list.size-1)) {
                if(!SquadUnit.equalsWithoutHP(list[i], list[i+1])) {
                    return false
                }
            }
        }
        return true
    }
}