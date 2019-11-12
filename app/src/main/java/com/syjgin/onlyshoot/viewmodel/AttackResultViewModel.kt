package com.syjgin.onlyshoot.viewmodel

import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum

class AttackResultViewModel : BaseViewModel() {
    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}