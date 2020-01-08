package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.BurstType
import com.syjgin.onlyshoot.model.DamageType
import com.syjgin.onlyshoot.model.Weapon
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AddEditWeaponViewModel : BaseViewModel() {
    private var weaponId: Long = NO_DATA
    private var isCopyMode = false
    private val weaponLiveData = MutableLiveData<Weapon>()

    fun getWeaponLiveData(): LiveData<Weapon> = weaponLiveData

    fun loadWeapon(id: Long) {
        weaponId = id
        viewModelScope.launch {
            val weapon = database.weaponDao().getById(weaponId)
            weaponLiveData.postValue(weapon)
        }
    }

    fun saveWeapon(
        name: String,
        attackModifier: Int,
        damage: Int,
        damageModifier: Int,
        attackCount: Int,
        missPossibility: Int,
        criticalHitModifier: Int,
        rage: Int,
        damageType: DamageType,
        armorPenetrationAmount: Int,
        armorPenetrationModifier: Int,
        burstType: BurstType,
        unitId: Long
    ) {
        if (weaponId == NO_DATA || isCopyMode) {
            weaponId = DbUtils.generateLongUUID()
        }
        val weapon = Weapon(
            weaponId,
            name,
            attackModifier,
            damage,
            damageModifier,
            attackCount,
            missPossibility,
            criticalHitModifier,
            rage,
            damageType,
            armorPenetrationAmount,
            armorPenetrationModifier,
            burstType
        )
        viewModelScope.launch {
            database.weaponDao().insert(weapon)
            if (unitId == NO_DATA) {
                router.exit()
            } else {
                weaponLiveData.postValue(weapon)
            }
        }
    }

    fun setCopyMode() {
        isCopyMode = true
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}