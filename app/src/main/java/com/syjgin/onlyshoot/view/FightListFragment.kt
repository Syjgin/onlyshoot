package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.model.Fight
import com.syjgin.onlyshoot.view.adapter.FightListAdapter
import com.syjgin.onlyshoot.viewmodel.FightListViewModel
import kotlinx.android.synthetic.main.fragment_fight_list.*

class FightListFragment : BaseFragment<FightListViewModel>(FightListViewModel::class.java),
    FightListAdapter.RemoveClickListener {

    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = FightListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var adapter : FightListAdapter? = null

    override fun fragmentTitle() = R.string.fight_list

    override fun fragmentLayout() = R.layout.fragment_fight_list

    override fun parseArguments(args: Bundle) {}

    override fun hasBackButton() = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel?.getFightList()?.observe(this, Observer { displayFightList(it) })
        add_fight.setOnClickListener {
            viewModel?.addFight()
        }
    }

    private fun displayFightList(it: List<Fight>) {
        if (adapter == null) {
            adapter = FightListAdapter(this@FightListFragment, it)
        }
        fight_recycler_view.adapter = adapter
        adapter?.updateData(it)
    }

    override fun removeClicked(fight: Fight) {
        viewModel?.removeItem(fight)
    }

    override fun itemClicked(fight: Fight) {
        viewModel?.openFight(fight)
    }
}