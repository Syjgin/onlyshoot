package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Attack
import com.syjgin.onlyshoot.model.AttackList
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch

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
            viewModelScope.launch {
                val attackersGroupList =
                    DbUtils.getGroupListBySquad(database.unitDao().getBySquad(attackersId))
                attackersLiveData.postValue(
                    Squad(
                        attackersGroupList,
                        true,
                        database.squadDescriptionDao().getById(attackersId)!!.name
                    )
                )
                val defendersGroupList =
                    DbUtils.getGroupListBySquad(database.unitDao().getBySquad(defendersId))
                defendersLiveData.postValue(
                    Squad(
                        defendersGroupList,
                        false,
                        database.squadDescriptionDao().getById(defendersId)!!.name
                    )
                )
            }
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
        viewModelScope.launch {
            val bundle = Bundle()
            val attacks2send = AttackList()
            val attackersSquad = database.unitDao().getBySquad(attackersId)
            val defendersSquad = database.unitDao().getBySquad(defendersId)
            for (attack in attacks) {
                val attackersUnitIds =
                    attackersSquad.filter { it.name.contains(attack.attackersGroupName) }
                val defendersUnitIds =
                    defendersSquad.filter { it.name.contains(attack.attackersGroupName) }
                attacks2send.add(
                    Attack(
                        attack.attackersGroupName,
                        attack.defendersGroupName,
                        attackersUnitIds.map { it.id },
                        defendersUnitIds.map { it.id },
                        attack.isRandom,
                        attack.count
                    )
                )
            }
            val moshiBuilder = Moshi.Builder().build()
            val jsonAdapter = moshiBuilder.adapter(AttackList::class.java)
            bundle.putString(BundleKeys.Attacks.name, jsonAdapter.toJson(attacks2send))
            bundle.putLong(BundleKeys.DefendSquadId.name, defendersId)
            router.navigateTo(OnlyShootScreen(ScreenEnum.AttackResult, bundle))
        }
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}