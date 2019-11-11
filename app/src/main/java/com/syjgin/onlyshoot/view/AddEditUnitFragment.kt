package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class AddEditUnitFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditUnitFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}