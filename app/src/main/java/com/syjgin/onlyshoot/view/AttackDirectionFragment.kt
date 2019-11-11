package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class AttackDirectionFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AttackDirectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}