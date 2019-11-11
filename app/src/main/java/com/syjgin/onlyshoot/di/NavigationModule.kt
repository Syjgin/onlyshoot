package com.syjgin.onlyshoot.di

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
class NavigationModule(private val router: Router) {
    @Provides
    @Singleton
    fun providesRouter() : Router = router
}