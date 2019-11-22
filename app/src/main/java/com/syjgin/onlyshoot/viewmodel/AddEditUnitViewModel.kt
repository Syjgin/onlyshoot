package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.DamageType
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.model.UnitArchetype
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AddEditUnitViewModel : BaseViewModel() {
    private val unitLiveData = MutableLiveData<SquadUnit>()
    private var unitId : Long = NO_DATA
    private var archetypeUnitId : Long = NO_DATA
    private var squadId : Long = NO_DATA
    private var unitName = ""

    fun getUnitLiveData() : LiveData<SquadUnit> = unitLiveData

    fun loadUnitData(unitId: Long, squadId: Long) {
        this.unitId = unitId
        this.squadId = squadId
        viewModelScope.launch {
            val squadUnit = database.unitDao().getById(unitId)
            if(squadUnit != null) {
                archetypeUnitId = squadUnit.parentId
                unitName = squadUnit.name
            }
            unitLiveData.postValue(squadUnit)
        }
    }

    fun saveUnit(
        name: String,
        attack: Int,
        attackModifier: Int,
        armorPenetration: Int,
        usualArmor: Int,
        proofArmor: Int,
        damage: Int,
        damageModifier: Int,
        damageType: DamageType,
        attackCount: Int,
        hp: Int,
        evasion: Int,
        evasionCount: Int,
        missPossibility: Int,
        criticalHitAvoidance: Int,
        criticalHitModifier: Int,
        canUseRage: Boolean,
        deathFromRage: Boolean,
        squadId: Long,
        rage: Int,
        isEditMode: Boolean
    ) {
        viewModelScope.launch {
            if(archetypeUnitId == NO_DATA) {
                archetypeUnitId = DbUtils.generateLongUUID()
                val archetype = UnitArchetype(
                    archetypeUnitId,
                    name,
                    attack,
                    attackModifier,
                    armorPenetration,
                    usualArmor,
                    proofArmor,
                    damage,
                    damageModifier,
                    damageType,
                    attackCount,
                    hp,
                    evasion,
                    evasionCount,
                    missPossibility,
                    criticalHitAvoidance,
                    criticalHitModifier,
                    canUseRage,
                    deathFromRage,
                    rage)
                database.archetypeDao().insert(archetype)
            }

            val squadUnit =
                SquadUnit(
                    if(isEditMode) unitId else DbUtils.generateLongUUID(),
                    archetypeUnitId,
                    name,
                    attack,
                    attackModifier,
                    armorPenetration,
                    usualArmor,
                    proofArmor,
                    damage,
                    damageModifier,
                    damageType,
                    attackCount,
                    hp,
                    evasion,
                    evasionCount,
                    missPossibility,
                    criticalHitAvoidance,
                    criticalHitModifier,
                    canUseRage,
                    deathFromRage,
                    squadId,
                    rage
                )
            database.unitDao().insert(squadUnit)
            router.exit()
        }
    }

    fun getArchetypeValues() {
        viewModelScope.launch {
            val archetype = database.archetypeDao().getById(archetypeUnitId)
            if(archetype != null) {
                val squadUnit = archetype.convertToSquadUnit(squadId, unitName)
                unitLiveData.postValue(squadUnit)
            }
        }
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}