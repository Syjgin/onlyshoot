package com.syjgin.onlyshoot.viewmodel

import com.syjgin.onlyshoot.di.OnlyShootApp

class SelectUnitViewModel : BaseViewModel() {
    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}