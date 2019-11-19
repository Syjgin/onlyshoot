package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AddEditFightViewModel : BaseViewModel() {
    private val attackLiveData = MutableLiveData<Squad>()
    private val defendLiveData = MutableLiveData<Squad>()
    private val saveDialogLiveData = MutableLiveData<Boolean>()
    private var attackersId : Long = NO_DATA
    private var defendersId : Long = NO_DATA
    private var isEditMode = false

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun getAttackFightData() : LiveData<Squad> {
        return attackLiveData
    }

    fun getDefendFightData() : LiveData<Squad> {
        return defendLiveData
    }

    fun renderFight(fight: Fight) {
        isEditMode = true
        attackersId = fight.firstSquadId
        defendersId = fight.secondSquadId
        viewModelScope.launch {
            refreshAttackers()
            refreshDefenders()
        }
    }

    fun setSquadAndReturn(squadId: Long, attackers: Boolean) {
        if(attackers) {
            attackersId = squadId
            viewModelScope.launch {
                refreshAttackers()
            }
        } else {
            defendersId = squadId
            viewModelScope.launch {
                refreshDefenders()
            }
        }
        router.exit()
    }

    override fun goBack() {
        if(isEditMode) {
            router.exit()
        } else {
            if(attackersId == NO_DATA || defendersId == NO_DATA) {
                router.exit()
            } else {
                saveDialogLiveData.postValue(true)
            }
        }
    }

    fun saveFight(name: String) {
        if(attackersId == NO_DATA || defendersId == NO_DATA) {
            router.exit()
            return
        }
        val fightId = DbUtils.generateLongUUID()
        val fight = Fight(fightId, name, attackersId, defendersId, System.currentTimeMillis())
        viewModelScope.launch {
            database.FightDao().insert(fight)
            router.exit()
        }
    }

    fun exit() {
        router.exit()
    }

    fun getSaveDialogLiveData() : LiveData<Boolean> {
        return saveDialogLiveData
    }

    fun loadSquad(attackers: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putBoolean(BundleKeys.SelectAttackers.name, attackers)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectSquad, bundle))
    }

    fun loadUnit(attackers: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.SelectAttackers.name, attackers)
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun openUnit(squadUnit: SquadUnit) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putSerializable(BundleKeys.Unit.name, squadUnit)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun duplicateUnit(squadUnit: SquadUnit, isAttackers: Boolean) {
        viewModelScope.launch {
            val targetUnit = database.UnitDao().getById(squadUnit.parentId)
            targetUnit.id = DbUtils.generateLongUUID()
            targetUnit.squadId = if(isAttackers) attackersId else defendersId
            database.UnitDao().insert(targetUnit)
            if(isAttackers) {
                viewModelScope.launch {
                    refreshAttackers()
                }
            } else {
                viewModelScope.launch {
                    refreshDefenders()
                }
            }
        }
    }

    fun startAttack() {
        val bundle = Bundle()
        bundle.putLong(BundleKeys.AttackSquadId.name, attackersId)
        bundle.putLong(BundleKeys.DefendSquadId.name, defendersId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AttackDirection, bundle))
    }

    fun swap() {
        val tempId = attackersId
        attackersId = defendersId
        defendersId = tempId
        viewModelScope.launch {
            refreshAttackers()
            refreshDefenders()
        }
    }

    private suspend fun refreshDefenders() {
        defendLiveData.postValue(
            Squad.createFromUnitList(
                database.UnitDao().getBySquad(defendersId),
                defendersId,
                database.SquadDescriptionDao().getById(defendersId)!!.name
            )
        )
    }

    private suspend fun refreshAttackers() {
        attackLiveData.postValue(
            Squad.createFromUnitList(
                database.UnitDao().getBySquad(attackersId),
                attackersId,
                database.SquadDescriptionDao().getById(attackersId)!!.name
            )
        )
    }

    fun removeUnit(squadUnit: SquadUnit, attackers: Boolean) {
        viewModelScope.launch {
            squadUnit.squadId = NO_DATA
            database.UnitDao().insert(squadUnit)
            if(attackers) {
                refreshAttackers()
            } else {
                refreshDefenders()
            }
        }
    }
}