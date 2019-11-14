package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.SelectSquadViewModel

class SelectSquadFragment : BaseFragment<SelectSquadViewModel>(SelectSquadViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SelectSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.select_squad

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle?) {

    }

    override fun hasBackButton() = true
}