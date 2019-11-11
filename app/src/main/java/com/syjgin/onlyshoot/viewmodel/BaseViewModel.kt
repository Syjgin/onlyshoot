package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.ViewModel
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Database
import ru.terrakok.cicerone.Router
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
    @Inject
    protected lateinit var database: Database
    @Inject
    protected lateinit var router: Router
}