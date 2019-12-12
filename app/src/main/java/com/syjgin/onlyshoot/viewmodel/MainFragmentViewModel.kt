package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum

class MainFragmentViewModel : BaseViewModel() {
    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun openSquads() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.ListMode.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectSquad, bundle))
    }

    fun openUnits() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.ListMode.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectUnit, bundle))
    }

    fun openFights() {
        router.navigateTo(OnlyShootScreen(ScreenEnum.FightList))
    }
}