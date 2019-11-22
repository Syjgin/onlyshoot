package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.SquadDescription
import com.syjgin.onlyshoot.navigation.BundleKeys
import com.syjgin.onlyshoot.view.adapter.SquadSelectAdapter
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel
import com.syjgin.onlyshoot.viewmodel.SelectSquadViewModel
import kotlinx.android.synthetic.main.fragment_select_squad.*

class SelectSquadFragment : BaseFragment<SelectSquadViewModel>(SelectSquadViewModel::class.java),
    SquadSelectAdapter.SquadSelectListener {

    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SelectSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var squad: SquadDescription? = null
    private var isAttackers = false
    private val adapter = SquadSelectAdapter(this)

    override fun fragmentTitle() = R.string.select_squad

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {
        isAttackers = args.getBoolean(BundleKeys.SelectAttackers.name)
    }

    override fun hasBackButton() = true

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add) {
            viewModel?.addSquad()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        select_squad.setOnClickListener {
            if(squad != null) {
                val addEditFightViewModel = ViewModelProviders.of(this).get(AddEditFightViewModel::class.java)
                addEditFightViewModel.setSquadAndReturn(squad!!.id, isAttackers)
            }
        }
        existing_squads.adapter = adapter
        viewModel?.getSquadsLiveData()?.observe(this, Observer { showSquads(it)})
    }

    private fun showSquads(squads: List<SquadDescription>) {
        adapter.addData(squads)
    }

    override fun squadSelected(squad: SquadDescription) {
        this.squad = squad
        select_squad.isEnabled = true
    }

    override fun squadEditClick(squad: SquadDescription) {
        viewModel?.startEditSquad(squad)
    }
}