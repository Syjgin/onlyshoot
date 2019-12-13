package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.UnitArchetype
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.utils.DbUtils.NO_DATA
import com.syjgin.onlyshoot.view.adapter.ArchetypeUnitListAdapter
import com.syjgin.onlyshoot.viewmodel.SelectUnitViewModel
import kotlinx.android.synthetic.main.fragment_select_squad.*

class SelectUnitFragment : BaseFragment<SelectUnitViewModel>(SelectUnitViewModel::class.java),
    ArchetypeUnitListAdapter.ArchetypeListClickListener {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SelectUnitFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var squadId = NO_DATA
    private var isListMode = false

    lateinit var adapter: ArchetypeUnitListAdapter

    override fun fragmentTitle() =
        if (isListMode) R.string.units else R.string.select_from_archetype

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {
        squadId = args.getLong(BundleKeys.SquadId.name)
        isListMode = args.getBoolean(BundleKeys.ListMode.name)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            viewModel?.addNewArchetype()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ArchetypeUnitListAdapter(this, isListMode)
        existing_squads.adapter = adapter
        if (isListMode) {
            select_squad.visibility = View.GONE
        } else {
            select_squad.setOnClickListener {
                viewModel?.addArchetypes(adapter.getCountMap(), squadId)
            }
        }
        viewModel?.getArchetypeLiveData()?.observe(this, Observer {
            renderData(it)
        })
    }

    override fun hasBackButton() = true

    override fun selectUnit(archetype: UnitArchetype) {
        viewModel?.archetypeSelected(archetype)
    }

    override fun archetypeCountChanged() {
        for (mapEntry in adapter.getCountMap()) {
            if (mapEntry.value > 0) {
                select_squad.isEnabled = true
                return
            }
        }
        select_squad.isEnabled = false
    }

    private fun renderData(data: List<UnitArchetype>) {
        adapter.addData(data)
    }
}