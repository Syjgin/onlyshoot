package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.UnitArchetype
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils

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
        for (archetypePair in map.entries) {
            for (i in 0..archetypePair.value) {
                DbUtils.duplicateArchetype(
                    viewModelScope,
                    database,
                    archetypePair.key,
                    squadId
                )
            }
        }
    }

    fun archetypeSelected(archetype: UnitArchetype) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putBoolean(BundleKeys.EditArchetype.name, false)
        bundle.putLong(BundleKeys.Unit.name, archetype.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }
}