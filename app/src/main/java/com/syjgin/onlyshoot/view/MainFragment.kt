package com.syjgin.onlyshoot.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment<MainFragmentViewModel>(MainFragmentViewModel::class.java) {

    companion object {
        fun createFragment(bundle: Bundle?): Fragment {
            val fragment = MainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.main

    override fun fragmentLayout() = R.layout.fragment_main

    override fun parseArguments(args: Bundle) {}

    override fun hasBackButton() = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        select_squad.setOnClickListener {
            viewModel?.openSquads()
        }
        select_unit.setOnClickListener {
            viewModel?.openUnits()
        }
        select_fights.setOnClickListener {
            viewModel?.openFights()
        }
        select_arsenal.setOnClickListener {
            viewModel?.openWeapons()
        }
    }
}