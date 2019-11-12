package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.FightListViewModel

class FightListFragment : BaseFragment<FightListViewModel>(FightListViewModel::class.java) {

    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = FightListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.fight_list

    override fun fragmentLayout() = R.layout.fragment_fight_list

    override fun parseArguments(args: Bundle?) {

    }
}