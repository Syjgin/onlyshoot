package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.viewmodel.AddEditSquadViewModel

class AddEditSquadFragment : BaseFragment<AddEditSquadViewModel>(AddEditSquadViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(arguments, R.string.add_squad, R.string.edit_squad)
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_squad

    override fun parseArguments(args: Bundle?) {

    }
}