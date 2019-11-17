package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Squad
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel
import com.syjgin.onlyshoot.viewmodel.SelectSquadViewModel
import kotlinx.android.synthetic.main.fragment_select_squad.*

class SelectSquadFragment : BaseFragment<SelectSquadViewModel>(SelectSquadViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SelectSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var squad: Squad? = null
    private var isAttackers = false

    override fun fragmentTitle() = R.string.select_squad

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle?) {}

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
                if(squad!!.list.isNotEmpty()) {
                    addEditFightViewModel.setSquadAndReturn(squad!!.list[0].squadId, isAttackers)
                }
            }
        }
    }
}