package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.model.UnitArchetype
import com.syjgin.onlyshoot.model.Weapon
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AddEditUnitViewModel : BaseViewModel() {
    private var unitLiveData = MutableLiveData<SquadUnit>()
    private var unitId : Long = NO_DATA
    private var archetypeUnitId : Long = NO_DATA
    private var squadId : Long = NO_DATA
    private var weaponId: Long = NO_DATA
    private var unitName = ""
    private var isArchetypeMode = false
    private var weaponLiveData = MutableLiveData<Weapon>()

    fun getUnitLiveData() : LiveData<SquadUnit> = unitLiveData

    fun getWeaponLiveData(): LiveData<Weapon> = weaponLiveData

    fun loadUnitData(unitId: Long, squadId: Long) {
        if (unitId == this.unitId)
            return
        this.unitId = unitId
        this.squadId = squadId
        viewModelScope.launch {
            val squadUnit = database.unitDao().getById(unitId)
            if(squadUnit != null) {
                archetypeUnitId = squadUnit.parentId
                unitName = squadUnit.name
                weaponId = squadUnit.weaponId
                weaponLiveData.postValue(database.weaponDao().getById(weaponId))
                unitLiveData.postValue(squadUnit)
            }
        }
    }

    fun saveUnit(
        name: String,
        attack: Int,
        attackModifier: Int,
        usualArmorHead: Int,
        proofArmorHead: Int,
        usualArmorTorso: Int,
        proofArmorTorso: Int,
        usualArmorLeftHand: Int,
        proofArmorLeftHand: Int,
        usualArmorRightHand: Int,
        proofArmorRightHand: Int,
        usualArmorLeftLeg: Int,
        proofArmorLeftLeg: Int,
        usualArmorRightLeg: Int,
        proofArmorRightLeg: Int,
        constDamageModifier: Int,
        tempDamageModifier: Int,
        hp: Int,
        evasion: Int,
        evasionCount: Int,
        criticalHitAvoidance: Int,
        canUseRage: Boolean,
        deathFromRage: Boolean,
        squadId: Long,
        weaponId: Long,
        weaponName: String,
        rage: Int,
        isEditMode: Boolean
    ) {
        viewModelScope.launch {
            if (archetypeUnitId == NO_DATA || isArchetypeMode) {
                if (archetypeUnitId == NO_DATA) {
                    archetypeUnitId = DbUtils.generateLongUUID()
                }
                val archetype = UnitArchetype(
                    archetypeUnitId,
                    name,
                    attack,
                    attackModifier,
                    usualArmorHead,
                    proofArmorHead,
                    usualArmorTorso,
                    proofArmorTorso,
                    usualArmorLeftHand,
                    proofArmorLeftHand,
                    usualArmorRightHand,
                    proofArmorRightHand,
                    usualArmorLeftLeg,
                    proofArmorLeftLeg,
                    usualArmorRightLeg,
                    proofArmorRightLeg,
                    constDamageModifier,
                    tempDamageModifier,
                    hp,
                    evasion,
                    evasionCount,
                    criticalHitAvoidance,
                    canUseRage,
                    deathFromRage,
                    weaponId,
                    weaponName,
                    rage)
                database.archetypeDao().insert(archetype)
                if (isArchetypeMode) {
                    goBack()
                    return@launch
                }
            }
            if (squadId != NO_DATA) {
                val squadUnit =
                    SquadUnit(
                        if (isEditMode) unitId else DbUtils.generateLongUUID(),
                        archetypeUnitId,
                        name,
                        attack,
                        attackModifier,
                        usualArmorHead,
                        proofArmorHead,
                        usualArmorTorso,
                        proofArmorTorso,
                        usualArmorLeftHand,
                        proofArmorLeftHand,
                        usualArmorRightHand,
                        proofArmorRightHand,
                        usualArmorLeftLeg,
                        proofArmorLeftLeg,
                        usualArmorRightLeg,
                        proofArmorRightLeg,
                        constDamageModifier,
                        tempDamageModifier,
                        hp,
                        evasion,
                        evasionCount,
                        criticalHitAvoidance,
                        canUseRage,
                        deathFromRage,
                        weaponId,
                        weaponName,
                        squadId,
                        rage
                    )
                database.unitDao().insert(squadUnit)
            }
            goBack()
        }
    }

    fun getArchetypeValues() {
        viewModelScope.launch {
            val archetype = database.archetypeDao().getById(archetypeUnitId)
            if(archetype != null) {
                weaponId = archetype.weaponId
                weaponLiveData.postValue(database.weaponDao().getById(weaponId))
                val squadUnit = archetype.convertToSquadUnit(squadId, unitName)
                unitLiveData.postValue(squadUnit)
            }
        }
    }

    fun loadArchetypeData(unitId: Long) {
        if (unitId == archetypeUnitId)
            return
        isArchetypeMode = true
        archetypeUnitId = unitId
        viewModelScope.launch {
            val archetypeUnit = database.archetypeDao().getById(unitId)
            if (archetypeUnit != null) {
                unitName = archetypeUnit.name
                weaponId = archetypeUnit.weaponId
                weaponLiveData.postValue(database.weaponDao().getById(weaponId))
            }
            unitLiveData.postValue(archetypeUnit?.convertToSquadUnit(NO_DATA, archetypeUnit.name))

        }
    }

    override fun goBack() {
        release()
        super.goBack()
    }

    override fun release() {
        super.release()
        unitLiveData = MutableLiveData()
        weaponLiveData = MutableLiveData()
        unitId = NO_DATA
        weaponId = NO_DATA
        archetypeUnitId = NO_DATA
        unitName = ""
        isArchetypeMode = false
    }

    fun selectWeapon() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.ListMode.name, false)
        if (weaponId != NO_DATA) {
            bundle.putLong(BundleKeys.WeaponId.name, weaponId)
        }
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectWeapon, bundle))
    }

    fun setWeapon(currentWeaponId: Long) {
        weaponId = currentWeaponId
        viewModelScope.launch {
            val weapon = database.weaponDao().getById(weaponId)
            weaponLiveData.postValue(weapon)
        }
    }

    fun getUnitId(): Long {
        return unitId
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}