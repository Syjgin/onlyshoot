package com.syjgin.onlyshoot.view

import android.os.Bundle
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

    val adapter = ArchetypeUnitListAdapter(this)

    override fun fragmentTitle() = R.string.select_from_archetype

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {
        squadId = args.getLong(BundleKeys.SquadId.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        existing_squads.adapter = adapter
        select_squad.setOnClickListener {
            viewModel?.addArchetypes(adapter.getCountMap(), squadId)
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