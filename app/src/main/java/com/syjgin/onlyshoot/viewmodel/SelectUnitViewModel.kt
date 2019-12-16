package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.UnitArchetype
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import kotlinx.coroutines.launch

class SelectUnitViewModel : BaseViewModel() {
    private val archetypeLiveData: LiveData<List<UnitArchetype>>

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
        archetypeLiveData = database.archetypeDao().getAll()
    }

    fun getArchetypeLiveData(): LiveData<List<UnitArchetype>> {
        return archetypeLiveData
    }

    fun addArchetypes(map: Map<UnitArchetype, Int>, squadId: Long) {
        viewModelScope.launch {
            for (archetypePair in map.entries) {
                for (i in 0 until archetypePair.value) {
                    val squad = database.unitDao().getBySquad(squadId)
                    val names = mutableListOf<String>()
                    for (currentUnit in squad) {
                        names.add(currentUnit.name)
                    }
                    val name = DbUtils.getNextUnitName(archetypePair.key.name, names)
                    val newUnit = archetypePair.key.convertToSquadUnit(
                        squadId,
                        name
                    )
                    Log.d("UNIT_ADD", newUnit.toString())
                    database.unitDao().insert(newUnit)
                }
            }
            router.exit()
        }
    }

    fun archetypeSelected(archetype: UnitArchetype) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putBoolean(BundleKeys.EditArchetype.name, true)
        bundle.putLong(BundleKeys.Unit.name, archetype.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun addNewArchetype() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        bundle.putBoolean(BundleKeys.EditArchetype.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun deleteArchetype(archetype: UnitArchetype) {
        viewModelScope.launch {
            val units = database.unitDao().getByArchetype(archetype.id)
            if (units.isNotEmpty()) {
                var isFightWithIncorrectUnitsFound = false
                val allFights = database.fightDao().getAllSuspend()
                for (unit in units) {
                    if (isFightWithIncorrectUnitsFound)
                        break
                    val squadId = unit.squadId
                    for (fight in allFights) {
                        if (fight.firstSquadId == squadId || fight.secondSquadId == squadId) {
                            isFightWithIncorrectUnitsFound = true
                            break
                        }
                    }
                }
                if (isFightWithIncorrectUnitsFound) {
                    Toast.makeText(
                        OnlyShootApp.getInstance().applicationContext,
                        OnlyShootApp.getInstance().applicationContext.getString(
                            R.string.cant_delete_archetype
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    for (unit in units) {
                        val squadId = unit.squadId
                        database.unitDao().delete(unit.id)
                        database.squadDescriptionDao().delete(squadId)
                    }
                    database.archetypeDao().delete(archetype)
                }
            } else {
                database.archetypeDao().delete(archetype)
            }
        }
    }
}