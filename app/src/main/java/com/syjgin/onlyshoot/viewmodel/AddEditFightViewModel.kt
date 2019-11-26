package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    companion object {
        const val REFRESH_DELAY = 500L
    }
    private val attackLiveData = MutableLiveData<Squad>()
    private val defendLiveData = MutableLiveData<Squad>()
    private val saveDialogLiveEvent = MutableLiveData<Boolean>()
    private var attackersId : Long = NO_DATA
    private var defendersId : Long = NO_DATA
    private var attackersSquad: Squad? = null
    private var defendersSquad: Squad? = null
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
        refreshAttackers()
        refreshDefenders()
    }

    fun setSquadAndReturn(squadId: Long, attackers: Boolean) {
        if(attackers) {
            attackersId = squadId
            Handler(Looper.getMainLooper()).postDelayed({
                refreshAttackers()
            }, REFRESH_DELAY)
            router.exit()
        } else {
            defendersId = squadId
            Handler(Looper.getMainLooper()).postDelayed({
                refreshDefenders()
            }, REFRESH_DELAY)
            router.exit()
        }
    }

    override fun goBack() {
        if(isEditMode) {
            router.exit()
        } else {
            if(attackersId == NO_DATA || defendersId == NO_DATA) {
                router.exit()
            } else {
                saveDialogLiveEvent.postValue(true)
            }
        }
    }

    fun saveFight(name: String) {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (attackersId == NO_DATA || defendersId == NO_DATA) {
                router.exit()
                return@Runnable
            }
            val fightId = DbUtils.generateLongUUID()
            val fight = Fight(fightId, name, attackersId, defendersId, System.currentTimeMillis())
            viewModelScope.launch {
                database.fightDao().insert(fight)
                router.exit()
            }
        }, REFRESH_DELAY)
    }

    fun getSaveDialogLiveData() : LiveData<Boolean> {
        return saveDialogLiveEvent
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
        DbUtils.duplicateUnit(
            viewModelScope,
            database,
            squadUnit,
            if (isAttackers) attackersId else defendersId
        ) {
            if(isAttackers) {
                refreshAttackers()
            } else {
                refreshDefenders()
            }
        }
    }

    fun startAttack() {
        if(attackersSquad == null || defendersSquad == null)
            return
        if(attackersSquad!!.isMembersIdentical() && defendersSquad!!.isMembersIdentical()) {
            val bundle = Bundle()
            bundle.putLong(BundleKeys.AttackSquadId.name, attackersId)
            bundle.putLong(BundleKeys.DefendSquadId.name, defendersId)
            router.navigateTo(OnlyShootScreen(ScreenEnum.AttackResult, bundle))
            return
        }
        val bundle = Bundle()
        bundle.putLong(BundleKeys.AttackSquadId.name, attackersId)
        bundle.putLong(BundleKeys.DefendSquadId.name, defendersId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AttackDirection, bundle))
    }

    fun swap() {
        val tempId = attackersId
        attackersId = defendersId
        defendersId = tempId
        if (attackersId != NO_DATA) {
            refreshAttackers()
        } else {
            attackLiveData.postValue(Squad(listOf(), true, ""))
        }
        if (defendersId != NO_DATA) {
            refreshDefenders()
        } else {
            defendLiveData.postValue(Squad(listOf(), false, ""))
        }
    }

    private fun refreshDefenders() {
        viewModelScope.launch {
            defendersSquad = Squad.createFromUnitList(
                database.unitDao().getBySquad(defendersId),
                defendersId,
                database.squadDescriptionDao().getById(defendersId)!!.name)
            defendLiveData.postValue(defendersSquad)
        }
    }

    private fun refreshAttackers() {
        viewModelScope.launch {
            attackersSquad = Squad.createFromUnitList(
                database.unitDao().getBySquad(attackersId),
                attackersId,
                database.squadDescriptionDao().getById(attackersId)!!.name)
            attackLiveData.postValue(attackersSquad)
        }
    }

    fun removeUnit(squadUnit: SquadUnit, attackers: Boolean) {
        viewModelScope.launch {
            squadUnit.squadId = NO_DATA
            database.unitDao().insert(squadUnit)
            if(attackers) {
                refreshAttackers()
            } else {
                refreshDefenders()
            }
        }
    }

    fun exitWithoutSaving() {
        Handler(Looper.getMainLooper()).postDelayed({
            router.exit()
        }, REFRESH_DELAY)
    }
}