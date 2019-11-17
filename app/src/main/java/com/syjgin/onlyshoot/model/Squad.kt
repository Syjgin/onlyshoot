package com.syjgin.onlyshoot.model

data class Squad(val list: List<SquadUnit>, val isAttackers: Boolean) {
    companion object {
        fun createFromUnitList(list: List<SquadUnit>, attackersId: Long) : Squad{
            return Squad(list, if(list.isEmpty()) false else list[0].squadId == attackersId)
        }
    }

    fun getId() : Long? {
        return if(list.isEmpty()) null else list[0].squadId
    }
}