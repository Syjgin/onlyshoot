package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Weapon
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA

class SelectWeaponViewModel : BaseViewModel() {
    private var weaponsLiveData: LiveData<List<Weapon>>

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
        weaponsLiveData = database.weaponDao().getAll()
    }

    fun getWeaponsLiveData(): LiveData<List<Weapon>> {
        return weaponsLiveData
    }

    fun addWeapon() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditWeapon, bundle))
    }

    fun startEditWeapon(weapon: Weapon) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.WeaponId.name, weapon.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditWeapon, bundle))
    }

    fun copyWeapon(weaponId: Long, unitId: Long) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putBoolean(BundleKeys.CopyMode.name, true)
        bundle.putLong(BundleKeys.WeaponId.name, weaponId)
        if (unitId != NO_DATA) {
            bundle.putLong(BundleKeys.Unit.name, unitId)
        }
        router.replaceScreen(OnlyShootScreen(ScreenEnum.AddEditWeapon, bundle))
    }

    fun startCopyWeapon() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.ListMode.name, false)
        bundle.putBoolean(BundleKeys.CopyMode.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectWeapon, bundle))
    }
}