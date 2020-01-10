package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.SquadArchetype
import com.syjgin.onlyshoot.model.SquadDescription
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AddEditSquadViewModel : BaseViewModel() {
    private var squadLiveData : LiveData<List<SquadUnit>>? = null
    private val nameLiveData = MutableLiveData<String>()
    private var isEditMode = false
    private var isArchetypeMode = false
    private var squadId = DbUtils.generateLongUUID()

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun loadSquad(id: Long, unitFilter: String, weaponFilter: Long, isArchetypeMode: Boolean) {
        squadId = id
        isEditMode = true
        this.isArchetypeMode = isArchetypeMode
        if (isArchetypeMode) {
            refreshArchetypes()
        } else {
            startObserveSquad(unitFilter, weaponFilter)
            viewModelScope.launch {
                val description = database.squadDescriptionDao().getById(squadId)
                if (description != null) {
                    nameLiveData.postValue(description.name)
                }
            }
        }
    }

    private fun refreshArchetypes() {
        if (squadLiveData == null) {
            squadLiveData = MutableLiveData<List<SquadUnit>>()
        }
        viewModelScope.launch {
            val entries = database.squadArchetypeDao().getSquadArchetypeSync(squadId)
            if (entries.isNotEmpty()) {
                nameLiveData.postValue(entries[0].name)
                val nameList = mutableListOf<String>()
                val data2display = mutableListOf<SquadUnit>()
                for (entry in entries) {
                    for (i in 0..entry.amount) {
                        val squadUnitArchetype =
                            database.archetypeDao().getById(entry.unitArchetypeId)!!
                        val nextName = DbUtils.getNextUnitName(squadUnitArchetype.name, nameList)
                        val squadUnit = squadUnitArchetype.convertToSquadUnit(squadId, nextName)
                        nameList.add(nextName)
                        data2display.add(squadUnit)
                    }
                }
                (squadLiveData as MutableLiveData<List<SquadUnit>>).postValue(data2display)
            }
        }
    }

    fun getSquadLiveData() : LiveData<List<SquadUnit>>? {
        return squadLiveData
    }

    fun getNameLiveData() : LiveData<String> = nameLiveData

    fun addUnit() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        bundle.putLong(BundleKeys.SquadId.name, squadId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun saveSquad(title: String) {
        viewModelScope.launch {
            if (squadLiveData == null)
                return@launch
            if (isArchetypeMode) {
                val entries = database.squadArchetypeDao().getSquadArchetypeSync(squadId)
                if (entries.isNotEmpty()) {
                    for (entry in entries) {
                        if (entry.name != title) {
                            entry.name = title
                            database.squadArchetypeDao().insert(entry)
                        }
                    }
                }
            } else {
                val squadDescription = database.squadDescriptionDao().getById(squadId)
                var needInsertArchetype = true
                var squadArchetypeId = DbUtils.generateLongUUID()
                if (squadDescription != null) {
                    val existingArchetype = database.squadArchetypeDao()
                        .getSquadArchetypeSync(squadDescription.archetypeId)
                    if (existingArchetype.isNotEmpty()) {
                        needInsertArchetype = false
                        squadArchetypeId = existingArchetype[0].squadId
                    }
                }
                if (needInsertArchetype) {
                    val units = database.unitDao().getBySquad(squadId)
                    val archetypeMap = mutableMapOf<Long, Int>()
                    for (currentUnit in units) {
                        if (!archetypeMap.containsKey(currentUnit.parentId)) {
                            archetypeMap[currentUnit.parentId] = 0
                        } else {
                            val oldValue = archetypeMap[currentUnit.parentId]!!
                            archetypeMap[currentUnit.parentId] = oldValue + 1
                        }
                    }
                    for (currentEntry in archetypeMap.entries) {
                        database.squadArchetypeDao().insert(
                            SquadArchetype(
                                0,
                                squadArchetypeId,
                                currentEntry.key,
                                currentEntry.value,
                                title
                            )
                        )
                    }
                }
                database.squadDescriptionDao()
                    .insert(SquadDescription(squadId, title, squadArchetypeId))
                for (member in squadLiveData!!.value!!) {
                    if (member.squadId != squadId) {
                        member.squadId = squadId
                        database.unitDao().insert(member)
                    }
                }
            }
            router.exit()
        }
    }

    fun openUnit(squadUnit: SquadUnit) {
        if (isArchetypeMode) {
            val bundle = Bundle()
            bundle.putBoolean(BundleKeys.AddFlavor.name, false)
            bundle.putBoolean(BundleKeys.EditArchetype.name, true)
            bundle.putLong(BundleKeys.Unit.name, squadUnit.parentId)
            router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
            return
        }
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.Unit.name, squadUnit.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun duplicateUnit(squadUnit: SquadUnit) {
        if (isArchetypeMode) {
            viewModelScope.launch {
                val archetypeEntries = database.squadArchetypeDao().getSquadArchetypeSync(squadId)
                for (entry in archetypeEntries) {
                    if (entry.unitArchetypeId == squadUnit.parentId) {
                        entry.amount = entry.amount + 1
                        database.squadArchetypeDao().insert(entry)
                        refreshArchetypes()
                        return@launch
                    }
                }
            }
        } else {
            DbUtils.duplicateUnit(
                squadUnit.weaponId,
                viewModelScope,
                database,
                squadUnit.parentId,
                squadId
            )
        }
    }

    fun removeUnit(squadUnit: SquadUnit) {
        viewModelScope.launch {
            if (isArchetypeMode) {
                val archetypeEntries = database.squadArchetypeDao().getSquadArchetypeSync(squadId)
                for (entry in archetypeEntries) {
                    if (entry.unitArchetypeId == squadUnit.parentId) {
                        if (entry.amount > 1) {
                            entry.amount = entry.amount - 1
                            database.squadArchetypeDao().insert(entry)
                        } else {
                            database.squadArchetypeDao().delete(entry.id)
                        }
                        refreshArchetypes()
                        return@launch
                    }
                }
            } else {
                database.unitDao().delete(squadUnit.id)
            }
        }
    }

    fun addArchetype() {
        val bundle = Bundle()
        bundle.putLong(BundleKeys.SquadId.name, squadId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectUnit, bundle))
    }

    fun startObserveSquad(unitFilter: String, weaponFilter: Long) {
        squadLiveData = if (unitFilter.isEmpty() && weaponFilter == NO_DATA) {
            database.unitDao().getBySquadLiveData(squadId)
        } else {
            database.unitDao().getBySquadLiveDataWithFilters(squadId, "%$unitFilter%", weaponFilter)
        }
    }

    fun resetToArchetype() {
        viewModelScope.launch {
            if (squadId == NO_DATA)
                return@launch
            val squad = database.squadDescriptionDao().getById(squadId)!!
            val squadUnits = database.unitDao().getBySquad(squadId)
            for (squadUnit in squadUnits) {
                database.unitDao().delete(squadUnit.id)
            }
            val archetypeList =
                database.squadArchetypeDao().getSquadArchetypeSync(squad.archetypeId)
            for (archetypeListEntry in archetypeList) {
                val unitArchetype =
                    database.archetypeDao().getById(archetypeListEntry.unitArchetypeId)!!
                val names = mutableListOf<String>()
                for (i in 0..archetypeListEntry.amount) {
                    val targetName = DbUtils.getNextUnitName(unitArchetype.name, names)
                    names.add(targetName)
                    val newUnit = unitArchetype.convertToSquadUnit(squadId, targetName)
                    database.unitDao().insert(newUnit)
                }
            }
        }
    }
}