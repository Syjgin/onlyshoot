package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadDescription
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.view.adapter.SquadSelectAdapter
import com.syjgin.onlyshoot.viewmodel.SelectSquadViewModel
import kotlinx.android.synthetic.main.fragment_select_squad.*

class SelectWeaponFragment : BaseFragment<SelectSquadViewModel>(SelectSquadViewModel::class.java),
    SquadSelectAdapter.SquadSelectListener {

    companion object {
        fun createFragment(bundle: Bundle?): Fragment {
            val fragment = SelectWeaponFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var isListMode = false
    private var currentWeaponId = NO_DATA
    private lateinit var adapter: SquadSelectAdapter

    override fun fragmentTitle() = if (isListMode) R.string.arsenal else R.string.select_weapon

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {
        if (args.containsKey(BundleKeys.WeaponId.name)) {
            currentWeaponId = args.getLong(BundleKeys.WeaponId.name)
        }
        isListMode = args.getBoolean(BundleKeys.ListMode.name)
    }

    override fun hasBackButton() = true

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            viewModel?.addSquad()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SquadSelectAdapter(this, isListMode)
        if (isListMode) {
            select_squad.visibility = View.GONE
        } else {
            select_squad.setOnClickListener {

            }
        }
        existing_squads.adapter = adapter
        viewModel?.getSquadsLiveData()?.observe(this, Observer { showSquads(it) })
    }

    private fun showSquads(squads: List<SquadDescription>) {
        adapter.addData(squads)
    }

    override fun squadSelected(squad: SquadDescription) {

        select_squad.isEnabled = true
    }

    override fun squadEditClick(squad: SquadDescription) {
        viewModel?.startEditSquad(squad)
    }
}