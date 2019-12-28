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
import com.syjgin.onlyshoot.model.UnitGroup
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import com.syjgin.onlyshoot.utils.DbUtils
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import kotlinx.coroutines.launch
import kotlin.random.Random

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
                val attackersSquadDescription =
                    database.squadDescriptionDao().getById(attackersId)!!.name
                DbUtils.getGroupListBySquad(
                    database.unitDao().getBySquad(attackersId),
                    database,
                    viewModelScope,
                    object : DbUtils.GroupsCallback {
                        override fun onGroupsCreationFinished(groups: List<UnitGroup>) {
                            attackersLiveData.postValue(
                                Squad(
                                    groups,
                                    true,
                                    attackersSquadDescription
                                )
                            )
                        }
                    })
                val defendersSquadDescription =
                    database.squadDescriptionDao().getById(defendersId)!!.name
                DbUtils.getGroupListBySquad(
                    database.unitDao().getBySquad(defendersId),
                    database,
                    viewModelScope,
                    object : DbUtils.GroupsCallback {
                        override fun onGroupsCreationFinished(groups: List<UnitGroup>) {
                            attackersLiveData.postValue(
                                Squad(
                                    groups,
                                    false,
                                    defendersSquadDescription
                                )
                            )
                        }
                    })
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
            val attacks2send = mutableListOf<Attack>()
            val attackersSquad = database.unitDao().getBySquad(attackersId)
            val defendersSquad = database.unitDao().getBySquad(defendersId)
            val attackCountById = mutableMapOf<Long, Int>()
            /*for (attackUnit in attackersSquad) {
                attackCountById[attackUnit.id] = attackUnit.attackCount
            }*/
            for (attack in attacks) {
                val attackersUnitIds =
                    attackersSquad.filter { it.name.contains(attack.attackersGroupName) }
                val defendersUnitIds =
                    defendersSquad.filter { it.name.contains(attack.attackersGroupName) }
                val attackCountByUnit = attack.count / attackersUnitIds.size
                /*for (attacker in attackersUnitIds) {
                    val freeAttacks = attacker.attackCount - attackCountByUnit
                    if (freeAttacks > 0) {
                        attackCountById[attacker.id] = freeAttacks
                    } else {
                        attackCountById.remove(attacker.id)
                    }
                }*/
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
            val random = Random(System.currentTimeMillis())
            while (attackCountById.isNotEmpty()) {
                val currentUnitId = attackCountById.keys.first()
                val currentFreeAttackCount = attackCountById[currentUnitId]!!
                val defenderId = defendersSquad[random.nextInt(defendersSquad.size)].id
                val attack = Attack(
                    "",
                    "",
                    listOf(currentUnitId),
                    listOf(defenderId),
                    true,
                    currentFreeAttackCount
                )
                attacks2send.add(attack)
                attackCountById.remove(currentUnitId)
            }
            val moshiBuilder = Moshi.Builder().build()
            val jsonAdapter = moshiBuilder.adapter(AttackList::class.java)
            val attackString = jsonAdapter.toJson(AttackList(attacks2send))
            bundle.putString(BundleKeys.Attacks.name, attackString)
            bundle.putLong(BundleKeys.DefendSquadId.name, defendersId)
            router.navigateTo(OnlyShootScreen(ScreenEnum.AttackResult, bundle))
        }
    }

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
    }


}