package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.utils.AddEditUtils
import com.syjgin.onlyshoot.viewmodel.AddEditFightViewModel

class AddEditFightFragment : BaseFragment<AddEditFightViewModel>(AddEditFightViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditFightFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle(): Int {
        return AddEditUtils.getAddEditFragmentTitle(arguments, R.string.add_fight,R.string.edit_fight)
    }

    override fun fragmentLayout() = R.layout.fragment_add_edit_fight

    override fun parseArguments(args: Bundle?) {

    }

    override fun hasBackButton() = true
}