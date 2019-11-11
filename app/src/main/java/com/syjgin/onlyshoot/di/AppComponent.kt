package com.syjgin.onlyshoot.di

import com.syjgin.onlyshoot.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DatabaseModule::class, NavigationModule::class])
@Singleton
interface AppComponent {
    fun inject(viewModel: MainViewModel)
}