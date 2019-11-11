package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class AttackResultFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AttackResultFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}