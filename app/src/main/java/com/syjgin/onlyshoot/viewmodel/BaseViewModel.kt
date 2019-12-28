package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.ViewModel
import com.syjgin.onlyshoot.model.Database
import ru.terrakok.cicerone.Router
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
    protected var isStarted = false
    @Inject
    protected lateinit var database: Database
    @Inject
    protected lateinit var router: Router

    fun onCreate() {
        if(!isStarted) {
            isStarted = true
            onFirstLaunch()
        }
    }

    open fun onFirstLaunch() {

    }

    open fun release() {

    }

    open fun goBack() {
        router.exit()
    }
}