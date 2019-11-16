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
import kotlinx.coroutines.launch

class AddEditFightViewModel : BaseViewModel() {
    companion object {
        const val NO_DATA = -1L
    }
    private val attackLiveData = MutableLiveData<Squad>()
    private val defendLiveData = MutableLiveData<Squad>()
    private var attackersId : Long = NO_DATA
    private var defendersId : Long = NO_DATA

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
        attackersId = fight.firstSquadId
        defendersId = fight.secondSquadId
        viewModelScope.launch {
            attackLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(attackersId), attackersId))
            defendLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(defendersId), defendersId))
        }
    }

    fun setSquad(squadId: Long, attackers: Boolean) {
        if(attackers) {
            attackersId = squadId
            viewModelScope.launch {
                attackLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(attackersId), attackersId))
            }
        } else {
            defendersId = squadId
            viewModelScope.launch {
                defendLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(defendersId), defendersId))
            }
        }
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
                    attackLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(attackersId), attackersId))
                }
            } else {
                viewModelScope.launch {
                    defendLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(defendersId), defendersId))
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
            attackLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(attackersId), attackersId))
            defendLiveData.postValue(Squad.createFromUnitList(database.UnitDao().getBySquad(defendersId), defendersId))
        }
    }
}