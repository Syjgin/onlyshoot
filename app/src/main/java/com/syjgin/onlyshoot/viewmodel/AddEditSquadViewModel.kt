package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.SquadDescription
import com.syjgin.onlyshoot.model.SquadUnit
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AddEditSquadViewModel : BaseViewModel() {
    private var squadLiveData : LiveData<List<SquadUnit>>? = null
    private val nameLiveData = MutableLiveData<String>()
    private var isEditMode = false
    private var squadId = DbUtils.generateLongUUID()

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }

    fun loadSquad(id: Long) {
        squadId = id
        isEditMode = true
        squadLiveData = database.UnitDao().getBySquadLiveData(squadId)
        viewModelScope.launch {
            val description = database.SquadDescriptionDao().getById(squadId)
            if(description != null) {
                nameLiveData.postValue(description.name)
            }
        }
    }

    fun getSquadLiveData() : LiveData<List<SquadUnit>>? {
        return squadLiveData
    }

    fun getNameLiveData() : LiveData<String> = nameLiveData

    fun addUnit() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun saveSquad(title: String, members: List<SquadUnit>) {
        viewModelScope.launch {
            if(isEditMode) {
                database.SquadDescriptionDao().insert(SquadDescription(squadId, title))
                for(member in members) {
                    if(member.squadId != squadId) {
                        member.squadId = squadId
                        database.UnitDao().insert(member)
                    }
                }
            } else {
                database.SquadDescriptionDao().insert(SquadDescription(squadId, title))
                for(member in members) {
                    if(member.squadId != squadId) {
                        member.squadId = squadId
                        database.UnitDao().insert(member)
                    }
                }
            }
        }
    }

    fun openUnit(squadUnit: SquadUnit) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.Unit.name, squadUnit.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun duplicateUnit(squadUnit: SquadUnit) {
        viewModelScope.launch {
            val targetUnit = database.UnitDao().getById(squadUnit.parentId)
            targetUnit.id = DbUtils.generateLongUUID()
            targetUnit.squadId = squadId
            database.UnitDao().insert(targetUnit)

        }
    }

    fun removeUnit(squadUnit: SquadUnit) {
        viewModelScope.launch {
            squadUnit.squadId = NO_DATA
            database.UnitDao().insert(squadUnit)
        }
    }
}