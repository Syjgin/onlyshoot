package com.syjgin.onlyshoot.view

import android.os.Bundle
import androidx.fragment.app.Fragment

class AddEditSquadFragment : Fragment() {
    companion object {
        fun createFragment(bundle: Bundle?) : Fragment {
            val fragment = AddEditSquadFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}