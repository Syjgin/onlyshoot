package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = SettingsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}