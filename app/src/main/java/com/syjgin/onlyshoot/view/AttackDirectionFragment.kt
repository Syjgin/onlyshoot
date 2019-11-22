package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.syjgin.onlyshoot.R
import com.syjgin.onlyshoot.viewmodel.AttackDirectionViewModel

class AttackDirectionFragment : BaseFragment<AttackDirectionViewModel>(AttackDirectionViewModel::class.java) {

    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AttackDirectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun fragmentTitle() = R.string.attack_direction

    override fun fragmentLayout() = R.layout.fragment_attack_direction

    override fun parseArguments(args: Bundle) {

    }

    override fun hasBackButton() = true
}