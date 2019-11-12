package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.AttackResultViewModel

class AttackResultFragment : BaseFragment<AttackResultViewModel>(AttackResultViewModel::class.java) {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AttackResultFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.attack_result

    override fun fragmentLayout() = R.layout.fragment_attack_result

    override fun parseArguments(args: Bundle?) {

    }
}