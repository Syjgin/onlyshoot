package com.syjgin.onlyshoot.viewmodel

import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum

class MainViewModel : BaseViewModel() {
    private var isStarted = false
    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun onCreate() {
        if(!isStarted) {
            isStarted = true
            router.newRootScreen(OnlyShootScreen(ScreenEnum.FightList))
        }
    }
}