package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.UnitArchetype
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
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
                var nameCount = 0
                val squad = database.unitDao().getBySquad(squadId)
                for (currentUnit in squad) {
                    if (currentUnit.name.startsWith(archetypePair.key.name)) {
                        nameCount++
                    }
                }
                for (i in 0 until archetypePair.value) {
                    val newUnit = archetypePair.key.convertToSquadUnit(
                        squadId,
                        archetypePair.key.name + nameCount
                    )
                    nameCount++
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
}