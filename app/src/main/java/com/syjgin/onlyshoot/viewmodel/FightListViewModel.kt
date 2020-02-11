package com.syjgin.onlyshoot.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.syjgin.onlyshoot.di.OnlyShootApp
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.navigation.OnlyShootScreen
import com.syjgin.onlyshoot.navigation.ScreenEnum
import kotlinx.coroutines.launch

class FightListViewModel : BaseViewModel() {
    private val fightList : LiveData<List<Fight>>

    init {
        OnlyShootApp.getInstance().getAppComponent().inject(this)
        fightList = database.fightDao().getAll()
    }

    fun getFightList() : LiveData<List<Fight>> {
        return fightList
    }

    fun removeItem(fight: Fight) {
        viewModelScope.launch {
            val firstSquadId = fight.firstSquadId
            val secondSquadId = fight.secondSquadId
            val squads = database.squadDescriptionDao().getAllSuspend()
            val fights = database.fightDao().getAllSuspend()
            var isSquadInOtherFights = false
            for (currentFight in fights) {
                if (currentFight.id != fight.id) {
                    if (currentFight.firstSquadId == firstSquadId ||
                        currentFight.secondSquadId == firstSquadId ||
                        currentFight.firstSquadId == secondSquadId ||
                        currentFight.secondSquadId == secondSquadId
                    ) {
                        isSquadInOtherFights = true
                    }
                }
            }
            database.fightDao().delete(fight)
            if (!isSquadInOtherFights) {
                var isFirstFound = false
                var isSecondFound = false
                for (squad in squads) {
                    if (squad.id == firstSquadId) {
                        isFirstFound = true
                        database.squadDescriptionDao().delete(squad.id)
                    }
                    if (squad.id == secondSquadId) {
                        isSecondFound = true
                        database.squadDescriptionDao().delete(squad.id)
                    }
                    if (isFirstFound && isSecondFound) {
                        break
                    }
                }
            }
        }
    }

    fun addFight() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.AddFlavor.name, true)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditFight, bundle))
    }

    fun openFight(fight: Fight) {
        val bundle = Bundle()
        bundle.putSerializable(BundleKeys.Fight.name, fight)
        router.navigateTo(OnlyShootScreen(ScreenEnum.AddEditFight, bundle))
    }
}