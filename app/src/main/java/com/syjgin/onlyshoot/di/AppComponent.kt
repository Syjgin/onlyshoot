package com.syjgin.onlyshoot.di

import com.syjgin.onlyshoot.viewmodel.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DatabaseModule::class, NavigationModule::class])
@Singleton
interface AppComponent {
    fun inject(viewModel: MainViewModel)
    fun inject(viewModel: MainFragmentViewModel)
    fun inject(viewModel: FightListViewModel)
    fun inject(viewModel: AddEditFightViewModel)
    fun inject(viewModel: AddEditSquadViewModel)
    fun inject(viewModel: AddEditUnitViewModel)
    fun inject(viewModel: AttackDirectionViewModel)
    fun inject(viewModel: AttackResultViewModel)
    fun inject(viewModel: SelectSquadViewModel)
    fun inject(viewModel: SelectUnitViewModel)
    fun inject(viewModel: SelectWeaponViewModel)
    fun inject(viewModel: AddEditWeaponViewModel)
}