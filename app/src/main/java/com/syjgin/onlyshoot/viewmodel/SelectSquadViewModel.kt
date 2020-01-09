package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.SquadDescription
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import kotlinx.coroutines.launch

class SelectSquadViewModel : BaseViewModel() {
    private var isArchetypeMode = false
    private var squadsLiveData: LiveData<List<SquadDescription>>? = null

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun getSquadsLiveData(): LiveData<List<SquadDescription>>? {
        return squadsLiveData
    }

    fun addSquad() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        bundle.putBoolean(BundleKeys.ArchetypeMode.name, isArchetypeMode)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }

    fun startEditSquad(squad: SquadDescription) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.SquadId.name, squad.id)
        bundle.putBoolean(BundleKeys.ArchetypeMode.name, isArchetypeMode)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }

    fun selectArchetype(archetypeId: Long, callback: ((Long) -> Unit)?) {
        DbUtils.createSquadByArchetype(archetypeId, viewModelScope, database, callback)
    }

    fun setArchetypeMode(archetypeMode: Boolean, subscribeCallback: () -> Unit) {
        isArchetypeMode = archetypeMode
        if (archetypeMode) {
            viewModelScope.launch {
                val ids = database.squadArchetypeDao().getIds()
                val list = mutableListOf<SquadDescription>()
                for (id in ids) {
                    val squadEntry = database.squadArchetypeDao().getSquadArchetypeSync(id)
                    if (squadEntry.isNotEmpty()) {
                        val squadName = squadEntry[0].name
                        list.add(SquadDescription(id, squadName, id))
                    }
                }
                squadsLiveData = MutableLiveData<List<SquadDescription>>()
                subscribeCallback()
                (squadsLiveData as MutableLiveData<List<SquadDescription>>).postValue(list)
            }
        } else {
            squadsLiveData = database.squadDescriptionDao().getAll()
            subscribeCallback()
        }
    }
}