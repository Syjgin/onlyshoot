package com.syjgin.onlyshoot.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.view.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

class OnlyShootScreen(private val screen: ScreenEnum, private val bundle: Bundle? = null) : SupportAppScreen() {
    override fun getFragment(): Fragment {
        return when(screen) {
            ScreenEnum.FightList -> FightListFragment.createFragment(bundle)
            ScreenEnum.AddEditFight -> AddEditFightFragment.createFragment(bundle)
            ScreenEnum.SelectSquad -> SelectSquadFragment.createFragment(bundle)
            ScreenEnum.AddEditSquad -> AddEditSquadFragment.createFragment(bundle)
            ScreenEnum.AddEditUnit -> AddEditUnitFragment.createFragment(bundle)
            ScreenEnum.AttackDirection -> AttackDirectionFragment.createFragment(bundle)
            ScreenEnum.AttackResult -> AttackResultFragment.createFragment(bundle)
            ScreenEnum.SelectUnit -> SelectUnitFragment.createFragment(bundle)
        }
    }

    override fun getScreenKey(): String {
        return screen.name
    }
}