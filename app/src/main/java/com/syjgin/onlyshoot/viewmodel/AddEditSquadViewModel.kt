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
        startObserveSquad()
        viewModelScope.launch {
            val description = database.squadDescriptionDao().getById(squadId)
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
        bundle.putLong(BundleKeys.SquadId.name, squadId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun saveSquad(title: String) {
        viewModelScope.launch {
            if (squadLiveData == null)
                return@launch
            database.squadDescriptionDao().insert(SquadDescription(squadId, title))
            for (member in squadLiveData!!.value!!) {
                if (member.squadId != squadId) {
                    member.squadId = squadId
                    database.unitDao().insert(member)
                }
            }
            router.exit()
        }
    }

    fun openUnit(squadUnit: SquadUnit) {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, false)
        bundle.putLong(BundleKeys.Unit.name, squadUnit.id)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditUnit, bundle))
    }

    fun duplicateUnit(squadUnit: SquadUnit) {
        DbUtils.duplicateUnit(viewModelScope, database, squadUnit.parentId, squadId)
    }

    fun removeUnit(squadUnit: SquadUnit) {
        viewModelScope.launch {
            database.unitDao().delete(squadUnit.id)
        }
    }

    fun addArchetype() {
        val bundle = Bundle()
        bundle.putLong(BundleKeys.SquadId.name, squadId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.SelectUnit, bundle))
    }

    fun startObserveSquad() {
        squadLiveData = database.unitDao().getBySquadLiveData(squadId)
    }
}