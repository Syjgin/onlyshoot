package com.syjgin.onlyshoot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

class AttackDirectionViewModel : BaseViewModel() {
    private val attackersLiveData = MutableLiveData<Squad>()
    private val defendersLiveData = MutableLiveData<Squad>()
    private var attackersId: Long = NO_DATA

    fun loadData(attackersId: Long, defendersId: Long) {
        this.attackersId = attackersId
        viewModelScope.launch {
            val attackersSquad = database.unitDao().getBySquad(attackersId)
            attackersLiveData.postValue(Squad.createFromUnitList(attackersSquad, attackersId, ""))
            val defendersSquad = database.unitDao().getBySquad(defendersId)
            defendersLiveData.postValue(Squad.createFromUnitList(defendersSquad, attackersId, ""))
        }
    }

    fun getAttackersLiveData(): LiveData<Squad> {
        return attackersLiveData
    }

    fun getDefendersLiveData(): LiveData<Squad> {
        return defendersLiveData
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}