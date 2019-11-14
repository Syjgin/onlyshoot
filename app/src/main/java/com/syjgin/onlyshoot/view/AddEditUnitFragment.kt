package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.viewmodel.AddEditUnitViewModel

class AddEditUnitFragment : BaseFragment<AddEditUnitViewModel>(AddEditUnitViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditUnitFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(arguments, R.string.add_squad, R.string.edit_squad)
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_unit

    override fun parseArguments(args: Bundle?) {

    }

    override fun hasBackButton() = true
}