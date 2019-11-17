package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum

class SelectSquadViewModel : BaseViewModel() {
    private var squadsLivedata = MutableLiveData<List<Squad>>()
    private val observer = Observer<List<SquadUnit>> {
        val map = ArrayMap<Long, MutableList<SquadUnit>>()
        for(squadUnit in it) {
            if(!map.contains(squadUnit.squadId)) {
                map[squadUnit.squadId] = mutableListOf(squadUnit)
            } else {
                map[squadUnit.squadId]?.add(squadUnit)
            }
        }
        val result = ArrayList<Squad>()
        for(keyValuePair in map.entries) {
            val currentSquad = Squad(keyValuePair.value, false)
            result.add(currentSquad)
        }
        squadsLivedata.postValue(result)
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun getSquadsLiveData() : LiveData<List<Squad>> {
        return squadsLivedata
    }

    fun loadData() {
        database.UnitDao().getAllBySquads().observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        database.UnitDao().getAllBySquads().removeObserver(observer)
    }

    fun addSquad() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }

    fun startEditSquad(squad: Squad) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.SquadId.name, squad.getId()!!)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }
}