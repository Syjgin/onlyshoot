package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.SquadDescription
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum

class SelectSquadViewModel : BaseViewModel() {
    private var squadsLiveData : LiveData<List<SquadDescription>>

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
        squadsLiveData = database.squadDescriptionDao().getAll()
    }

    fun getSquadsLiveData() : LiveData<List<SquadDescription>> {
        return squadsLiveData
    }

    fun addSquad() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }

    fun startEditSquad(squad: SquadDescription) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.SquadId.name, squad.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditSquad, bundle))
    }
}