package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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

    override fun parseArguments(args: Bundle?) {}

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun hasBackButton() = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.settings) {
            viewModel?.settingsSelected()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}