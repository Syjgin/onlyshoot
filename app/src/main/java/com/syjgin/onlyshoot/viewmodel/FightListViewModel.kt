package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum

class FightListViewModel : BaseViewModel() {
    private val fightList = MutableLiveData<List<Fight>>()
    private val fightListObserver = Observer<List<Fight>> {
        fightList.postValue(it)
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    override fun onFirstLaunch() {
        super.onFirstLaunch()
        database.FightDao().getAll().observeForever(fightListObserver)
    }

    override fun onCleared() {
        super.onCleared()
        database.FightDao().getAll().removeObserver(fightListObserver)
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
}