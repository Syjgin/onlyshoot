package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.SelectUnitViewModel

class SelectUnitFragment : BaseFragment<SelectUnitViewModel>(SelectUnitViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SelectUnitFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.select_from_archetype

    override fun fragmentLayout() = R.layout.fragment_select_squad

    override fun parseArguments(args: Bundle) {

    }

    override fun hasBackButton() = true
}