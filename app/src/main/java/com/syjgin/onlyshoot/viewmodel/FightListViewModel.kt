package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import kotlinx.coroutines.launch

class FightListViewModel : BaseViewModel() {
    private val fightList = MutableLiveData<List<Fight>>()

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun refreshList() {
        viewModelScope.launch {
            fightList.postValue(database.FightDao().getAll())
        }
    }

    fun settingsSelected() {
        router.navigateTo(OnlyShootScreen(ScreenEnum.Settings))
    }

    fun getFightList() : LiveData<List<Fight>> {
        return fightList
    }

    fun removeItem(fight: Fight) {
        database.FightDao().delete(fight)
    }

    fun addFight() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditFight, bundle))
    }

    fun openFight(fight: Fight) {
        val bundle = Bundle()
        bundle.putLong(BundleKeys.Fight.name, fight.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditFight, bundle))
    }
}