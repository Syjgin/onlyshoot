package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA

class AttackDirectionViewModel : BaseViewModel() {
    private val attackersLiveData = MutableLiveData<Squad>()
    private val defendersLiveData = MutableLiveData<Squad>()
    private var attackersId: Long = NO_DATA
    private var defendersId: Long = NO_DATA
    private var alreadyLoaded = false

    fun loadData(attackersId: Long, defendersId: Long) {
        if(!alreadyLoaded) {
            this.attackersId = attackersId
            this.defendersId = defendersId
            /*viewModelScope.launch {
                val attackersSquad = database.unitDao().getBySquad(attackersId)
                attackersLiveData.postValue(Squad.createFromUnitList(attackersSquad, attackersId, ""))
                val defendersSquad = database.unitDao().getBySquad(defendersId)
                defendersLiveData.postValue(Squad.createFromUnitList(defendersSquad, attackersId, ""))
            }*/
            alreadyLoaded = true
        }
    }

    fun getAttackersLiveData(): LiveData<Squad> {
        return attackersLiveData
    }

    fun getDefendersLiveData(): LiveData<Squad> {
        return defendersLiveData
    }

    fun startAttack(attacks: List<Attack>) {
        val bundle = Bundle()
        val arrayList = ArrayList<Attack>(attacks)
        bundle.putParcelableArrayList(BundleKeys.Attacks.name, arrayList)
        bundle.putLong(BundleKeys.DefendSquadId.name, defendersId)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AttackResult, bundle))
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}