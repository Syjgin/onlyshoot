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
import com.syjgin.onlyshoot.model.UnitGroup
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch
import kotlin.random.Random

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
        updateSquads()
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
            release()
        } else {
            if(attackersId == NO_DATA || defendersId == NO_DATA) {
                release()
            } else {
                saveDialogLiveEvent.postValue(true)
            }
        }
    }

    fun saveFight(name: String) {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (attackersId == NO_DATA || defendersId == NO_DATA) {
                release()
                return@Runnable
            }
            val fightId = DbUtils.generateLongUUID()
            val fight = Fight(fightId, name, attackersId, defendersId, System.currentTimeMillis())
            viewModelScope.launch {
                database.fightDao().insert(fight)
                release()
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
        bundle.putLong(BundleKeys.SquadId.name, if (attackers) attackersId else defendersId)
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun loadUnitFromArchetype(attackers: Boolean) {
        val bundle = Bundle()
        bundle.putLong(BundleKeys.SquadId.name, if (attackers) attackersId else defendersId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectUnit, bundle))
    }

    fun openGroup(unitName: String, weaponId: Long, isAttackers: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putString(BundleKeys.GroupName.name, unitName)
        bundle.putLong(BundleKeys.WeaponId.name, weaponId)
        bundle.putLong(BundleKeys.SquadId.name, if (isAttackers) attackersId else defendersId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }

    fun duplicateUnit(archetypeId: Long, isAttackers: Boolean, weaponId: Long) {
        DbUtils.duplicateUnit(
            weaponId,
            viewModelScope,
            database,
            archetypeId,
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
            val squadDescription = database.squadDescriptionDao().getById(defendersId)
            DbUtils.getGroupListBySquad(
                database.unitDao().getBySquad(defendersId),
                database,
                viewModelScope, object : DbUtils.GroupsCallback {
                    override fun onGroupsCreationFinished(groups: List<UnitGroup>) {
                        defendersSquad = Squad(
                            groups,
                            false,
                            squadDescription?.name ?: ""
                        )
                        defendLiveData.postValue(defendersSquad)
                    }
                })
        }
    }

    private fun refreshAttackers() {
        viewModelScope.launch {
            val squadDescription = database.squadDescriptionDao().getById(attackersId)
            DbUtils.getGroupListBySquad(
                database.unitDao().getBySquad(attackersId),
                database,
                viewModelScope,
                object : DbUtils.GroupsCallback {
                    override fun onGroupsCreationFinished(groups: List<UnitGroup>) {
                        attackersSquad = Squad(
                            groups,
                            true,
                            squadDescription?.name ?: ""
                        )
                        attackLiveData.postValue(attackersSquad)
                    }
                }
            )
        }
    }

    fun removeUnit(groupName: String, attackers: Boolean) {
        viewModelScope.launch {
            val squadId = if (attackers) attackersId else defendersId
            val squad = database.unitDao().getBySquad(squadId)
            var unit2remove = squad[Random(System.currentTimeMillis()).nextInt(squad.size)]
            var minimalHp = Int.MAX_VALUE
            for (squadUnit in squad) {
                if (DbUtils.removeDigitsFrom(squadUnit.name) == groupName && squadUnit.hp < unit2remove.hp && squadUnit.hp < minimalHp) {
                    minimalHp = squadUnit.hp
                    unit2remove = squadUnit
                }
            }
            database.unitDao().delete(unit2remove.id)
            if(attackers) {
                refreshAttackers()
            } else {
                refreshDefenders()
            }
        }
    }

    fun exitWithoutSaving() {
        Handler(Looper.getMainLooper()).postDelayed({
            release()
        }, REFRESH_DELAY)
    }

    override fun release() {
        super.release()
        attackersId = NO_DATA
        defendersId = NO_DATA
        attackLiveData.postValue(Squad(listOf(), true, ""))
        defendLiveData.postValue(Squad(listOf(), false, ""))
        saveDialogLiveEvent.postValue(false)
        router.exit()
    }

    fun updateSquads() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (attackersId != NO_DATA)
                refreshAttackers()
            if (defendersId != NO_DATA)
                refreshDefenders()
        }, REFRESH_DELAY)
    }

    fun finishChangeWeapon(
        groupName: String,
        previousWeaponId: Long,
        nextWeaponId: Long,
        squadId: Long
    ) {
        viewModelScope.launch {
            DbUtils.changeWeaponForSquadGroup(
                squadId,
                groupName,
                previousWeaponId,
                nextWeaponId,
                database,
                viewModelScope
            )
        }
    }

    fun changeWeapon(groupName: String, weaponId: Long, attackers: Boolean) {
        viewModelScope.launch {
            val bundle = Bundle()
            bundle.putBoolean(BundleKeys.ListMode.name, false)
            if (weaponId != NO_DATA) {
                bundle.putLong(BundleKeys.WeaponId.name, weaponId)
            }
            bundle.putLong(BundleKeys.SquadId.name, if (attackers) attackersId else defendersId)
            bundle.putString(BundleKeys.GroupName.name, groupName)
            router.navigateTo(OnlyShootScreen(ScreenEnum.SelectWeapon, bundle))
        }
    }
}