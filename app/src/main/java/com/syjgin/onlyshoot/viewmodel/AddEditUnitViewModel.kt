package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.DamageType
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
    private val unitLiveData = MutableLiveData<SquadUnit>()
    private var unitId : Long = NO_DATA
    private var archetypeUnitId : Long = NO_DATA
    private var squadId : Long = NO_DATA
    private var weaponId: Long = NO_DATA
    private var unitName = ""
    private var isArchetypeMode = false
    private val weaponLiveData = MutableLiveData<Weapon>()

    fun getUnitLiveData() : LiveData<SquadUnit> = unitLiveData

    fun getWeaponLiveData(): LiveData<Weapon> = weaponLiveData

    fun loadUnitData(unitId: Long, squadId: Long) {
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
        armorPenetration: Int,
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
        damageType: DamageType,
        hp: Int,
        evasion: Int,
        evasionCount: Int,
        criticalHitAvoidance: Int,
        canUseRage: Boolean,
        deathFromRage: Boolean,
        squadId: Long,
        weaponId: Long,
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
                    armorPenetration,
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
                    damageType,
                    hp,
                    evasion,
                    evasionCount,
                    criticalHitAvoidance,
                    canUseRage,
                    deathFromRage,
                    weaponId,
                    rage)
                database.archetypeDao().insert(archetype)
                if (isArchetypeMode) {
                    router.exit()
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
                        armorPenetration,
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
                        damageType,
                        hp,
                        evasion,
                        evasionCount,
                        criticalHitAvoidance,
                        canUseRage,
                        deathFromRage,
                        squadId,
                        weaponId,
                        rage
                    )
                database.unitDao().insert(squadUnit)
            }
            router.exit()
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

    fun selectWeapon() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.ListMode.name, false)
        if (weaponId != NO_DATA) {
            bundle.putLong(BundleKeys.WeaponId.name, squadId)
        }
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectWeapon, bundle))
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}